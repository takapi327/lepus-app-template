package app.repository

import cats.effect.IO

import doobie.implicits.*

import lepus.logger.given
import lepus.database.{ DatabaseConfig, DoobieRepository, DBTransactor, DoobieQueryHelper }

import app.model.*

case class TaskCategoryRepository(database: DatabaseConfig)(using DBTransactor[IO]) extends DoobieRepository[IO], DoobieQueryHelper, CustomMapping:

  override val table = "todo_task_category"

  def get(id: Long): IO[Option[TaskCategory]] = Action.transact {
    select[TaskCategory].where(fr"id = $id").query[TaskCategory].option
  }

  def findByTaskId(taskId: Long): IO[Option[TaskCategory]] = Action.transact {
    select[TaskCategory].where(fr"task_id = $taskId").query[TaskCategory].option
  }

  def filterByTaskIds(taskIds: Seq[Long]): IO[Seq[TaskCategory]] = Action.transact {
    select[TaskCategory].where(fr"task_id IN(${taskIds.mkString(",")})").query[TaskCategory].to[Seq]
  }

  def filterByCategoryId(categoryId: Long): IO[Seq[TaskCategory]] = Action.transact {
    select[TaskCategory].where(fr"category_id = $categoryId").query[TaskCategory].to[Seq]
  }

  def add(data: TaskCategory): IO[Long] = Action.transact {
    insert[TaskCategory].values(fr"${data.id}", fr"${data.taskId}", fr"${data.categoryId}")
      .update
      .withUniqueGeneratedKeys[Long]("id")
  }

  def update(data: TaskCategory): IO[Int] = Action.transact {
    update.set(fr"task_id=${data.taskId}, category_id=${data.categoryId}")
      .where(fr"id=${data.id}")
      .updateRun
  }

  def deleteByTaskId(taskId: Long): IO[Int] = Action.transact {
    delete.where(fr"task_id = $taskId").updateRun
  }

  def deleteByCategoryId(categoryId: Long): IO[Int] = Action.transact {
    delete.where(fr"category_id = $categoryId").updateRun
  }
