package application.service

import cats.effect.IO

import cats.data.{ EitherT, NonEmptyList }

import lepus.database.*
import lepus.database.implicits.*

import application.model.{ JsValueCategory, JsValuePostTask, JsValuePutTask, JsValueTask }
import infrastructure.eduTodo.model.{ Category, Task, TaskCategory }
import infrastructure.eduTodo.repository.{ CategoryRepository, TaskCategoryRepository, TaskRepository }

class TaskService(
  taskRepository:         TaskRepository,
  categoryRepository:     CategoryRepository,
  taskCategoryRepository: TaskCategoryRepository,
)(using DatabaseModule[IO]):

  def getAll: IO[Seq[JsValueTask]] =
    (for
      taskSeq         <- taskRepository.findAll()
      taskCategorySeq <- NonEmptyList.fromList(taskSeq.flatMap(_.id)) match
        case Some(list) => taskCategoryRepository.filterByTaskIds(list)
        case None       => WeakAsyncConnectionIO.pure(List.empty)
      categorySeq     <- NonEmptyList.fromList(taskCategorySeq.map(_.categoryId)) match
        case Some(list) => categoryRepository.filterByIds(list)
        case None       => WeakAsyncConnectionIO.pure(List.empty)
    yield JsValueTask.buildMulti(taskSeq, taskCategorySeq, categorySeq)).transaction

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
    ))).transaction

  def add(task: JsValuePostTask): IO[Long] =
    (for
      taskId <- taskRepository.add(Task(None, task.title, task.description))
      _      <- task.categoryId match
        case Some(id) => taskCategoryRepository.add(TaskCategory.create(taskId, id))
        case None     => WeakAsyncConnectionIO.unit
    yield taskId).transaction("master")

  def update(id: Long, params: JsValuePutTask): EitherT[IO, Throwable, Unit] =
    (EitherT.fromOptionF[ConnectionIO, Throwable, Task](taskRepository.get(id), IllegalArgumentException("")) semiflatMap { task =>
      taskRepository.update(task.copy(
        title       = params.title,
        description = params.description,
        state       = params.state
      )).map(_ => task)
    } flatMap(task => updateTaskCategory(task, params))).transaction("master")

  def delete(id: Long): IO[Int] = taskRepository.delete(id).transaction("master")

  private def updateTaskCategory(task: Task, params: JsValuePutTask): EitherT[ConnectionIO, Throwable, Unit] =
    (params.categoryId match {
      case Some(cid) => EitherT.fromOptionF[ConnectionIO, Throwable, Category](categoryRepository.get(cid), IllegalArgumentException(s"Category with id $cid does not exist")).map(_ => ())
      case None      => EitherT.pure[ConnectionIO, Throwable](())
    }) semiflatMap { _ => for
      taskCategoryOpt <- taskCategoryRepository.findByTaskId(task.id.get)
      _               <- (params.categoryId, taskCategoryOpt, params.categoryId != taskCategoryOpt.map(_.categoryId)) match {
        case (Some(cid), None,      true)  => taskCategoryRepository.add(TaskCategory.create(task.id.get, cid)).map(_ => None)
        case (Some(cid), Some(old), true)  => taskCategoryRepository.update(old.copy(categoryId = cid))
        case (None,      Some(_),   _)     => taskCategoryRepository.deleteByTaskId(task.id.get)
        case _                             => WeakAsyncConnectionIO.pure(None)
      }
    yield ()}
