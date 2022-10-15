package app.service

import cats.effect.IO

import cats.data.EitherT

import app.model.{ Task, Category, TaskCategory }
import app.model.json.{ JsValueTask, JsValueCategory, JsValuePostTask, JsValuePutTask }
import app.repository.{ TaskRepository, CategoryRepository, TaskCategoryRepository }

class TaskService(
  taskRepository:         TaskRepository,
  categoryRepository:     CategoryRepository,
  taskCategoryRepository: TaskCategoryRepository,
):

  def getAll: IO[Seq[JsValueTask]] =
    for
      taskSeq         <- taskRepository.findAll()
      taskCategorySeq <- taskCategoryRepository.filterByTaskIds(taskSeq.map(_.id.get))
      categorySeq     <- categoryRepository.filterByIds(taskCategorySeq.map(_.categoryId))
    yield JsValueTask.buildMulti(taskSeq, taskCategorySeq, categorySeq)

  def get(id: Long): IO[Option[JsValueTask]] =
    for
      task            <- taskRepository.get(id)
      taskCategoryOpt <- taskCategoryRepository.findByTaskId(id)
      categoryOpt     <- taskCategoryOpt match
        case Some(v) => categoryRepository.get(v.categoryId)
        case None    => IO.pure(None)
    yield task.map(v => JsValueTask(
      id          = v.id,
      title       = v.title,
      description = v.description,
      state       = v.state,
      category    = categoryOpt.map(JsValueCategory.build _)
    ))

  def add(task: JsValuePostTask): IO[Long] =
    for
      taskId <- taskRepository.add(Task(None, task.title, task.description))
      _      <- task.categoryId match
        // めんどくさかった...
        case Some(id) => taskCategoryRepository.add(TaskCategory.create(taskId, id))
        case None     => IO.unit
    yield taskId

  def update(id: Long, params: JsValuePutTask): EitherT[IO, Throwable, Unit] =
    EitherT.fromOptionF(taskRepository.get(id), IllegalArgumentException("")) semiflatMap { task =>
      taskRepository.update(task.copy(
        title       = params.title,
        description = params.description,
        state       = params.state
      )) >> IO.pure(task)
    } flatMap { task => updateTaskCategory(task, params) }

  def delete(id: Long): IO[Int] = taskRepository.delete(id)

  private def updateTaskCategory(task: Task, params: JsValuePutTask): EitherT[IO, Throwable, Unit] =
    (params.categoryId match {
      case Some(cid) => EitherT.fromOptionF(categoryRepository.get(cid), IllegalArgumentException(s"$cid が存在しない")).map(_ => ())
      case None      => EitherT.pure[IO, Throwable](())
    }) semiflatMap { _ => for
      taskCategoryOpt <- taskCategoryRepository.findByTaskId(task.id.get)
      _               <- (params.categoryId, taskCategoryOpt, params.categoryId != taskCategoryOpt.map(_.categoryId)) match {
        case (Some(cid), None,      true)  => taskCategoryRepository.add(TaskCategory.create(task.id.get, cid)).map(_ => None)
        case (Some(cid), Some(old), true)  => taskCategoryRepository.update(old.copy(categoryId = cid))
        case (None,      Some(_),   _)     => taskCategoryRepository.deleteByTaskId(task.id.get)
        case _                             => IO.pure(None)
      }
    yield ()}
