package application.service

import javax.inject.{ Inject, Singleton }

import com.google.inject.name.Named

import cats.effect.IO

import cats.data.{ EitherT, NonEmptyList }

import lepus.doobie.*
import lepus.doobie.implicits.*

import application.model.{ JsValueCategory, JsValuePostTask, JsValuePutTask, JsValueTask }
import infrastructure.eduTodo.model.{ Category, Task, TaskCategory }
import infrastructure.eduTodo.repository.{ CategoryRepository, TaskCategoryRepository, TaskRepository }

@Singleton
class TaskService @Inject()(
  @Named("edu_todo_master") master: ContextIO,
  @Named("edu_todo_slave")  slave:  ContextIO,
  taskRepository:         TaskRepository,
  categoryRepository:     CategoryRepository,
  taskCategoryRepository: TaskCategoryRepository,
):

  def getAll: IO[Seq[JsValueTask]] =
    (for
      taskSeq         <- taskRepository.findAll()
      taskCategorySeq <- NonEmptyList.fromList(taskSeq.flatMap(_.id)) match
        case Some(list) => taskCategoryRepository.filterByTaskIds(list)
        case None       => WeakAsyncConnectionIO.pure(List.empty)
      categorySeq     <- NonEmptyList.fromList(taskCategorySeq.map(_.categoryId)) match
        case Some(list) => categoryRepository.filterByIds(list)
        case None       => WeakAsyncConnectionIO.pure(List.empty)
    yield JsValueTask.buildMulti(taskSeq, taskCategorySeq, categorySeq)).transact(slave.xa)

  def get(id: Long): IO[Option[JsValueTask]] =
    (for
      task            <- taskRepository.get(id)
      taskCategoryOpt <- taskCategoryRepository.findByTaskId(id)
      categoryOpt     <- taskCategoryOpt match
        case Some(v) => categoryRepository.get(v.categoryId)
        case None    => WeakAsyncConnectionIO.pure(None)
    yield task.map(v => JsValueTask(
      id          = v.id,
      title       = v.title,
      description = v.description,
      state       = v.state,
      category    = categoryOpt.map(JsValueCategory.build)
    ))).transact(slave.xa)

  def add(task: JsValuePostTask): IO[Long] =
    (for
      taskId <- taskRepository.add(Task(None, task.title, task.description))
      _      <- task.categoryId match
        case Some(id) => taskCategoryRepository.add(TaskCategory.create(taskId, id))
        case None     => WeakAsyncConnectionIO.unit
    yield taskId).transact(master.xa)

  def update(id: Long, params: JsValuePutTask): EitherT[IO, Throwable, Unit] =
    (EitherT.fromOptionF[ConnectionIO, Throwable, Task](taskRepository.get(id), IllegalArgumentException("")) semiflatMap { task =>
      taskRepository.update(task.copy(
        title       = params.title,
        description = params.description,
        state       = params.state
      )).as(task)
    } flatMap(task => updateTaskCategory(task, params))).transact(master.xa)

  def delete(id: Long): IO[Int] = taskRepository.delete(id).transact(master.xa)

  private def updateTaskCategory(task: Task, params: JsValuePutTask): EitherT[ConnectionIO, Throwable, Unit] =
    (params.categoryId match {
      case Some(cid) => EitherT.fromOptionF[ConnectionIO, Throwable, Category](categoryRepository.get(cid), IllegalArgumentException(s"Category with id $cid does not exist")).map(_ => ())
      case None      => EitherT.pure[ConnectionIO, Throwable](())
    }) semiflatMap { _ => taskCategoryRepository.findByTaskId(task.id.get).flatMap(taskCategoryOpt => {
      (params.categoryId, taskCategoryOpt, params.categoryId != taskCategoryOpt.map(_.categoryId)) match {
        case (Some(cid), None,      true)  => taskCategoryRepository.add(TaskCategory.create(task.id.get, cid)).void
        case (Some(cid), Some(old), true)  => taskCategoryRepository.update(old.copy(categoryId = cid)).void
        case (None,      Some(_),   _)     => taskCategoryRepository.deleteByTaskId(task.id.get).void
        case _                             => WeakAsyncConnectionIO.unit
      }
    })}
