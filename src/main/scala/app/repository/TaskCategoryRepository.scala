package app.repository

import cats.effect.IO

import doobie.implicits.*

import lepus.logger.given
import lepus.database.{ DatabaseConfig, DoobieRepository, DBTransactor, DoobieQueryHelper }

import app.model.*

class TaskCategoryRepository(using DBTransactor[IO]) extends DoobieRepository[IO], DoobieQueryHelper, CustomMapping:

  override val database = DatabaseConfig("lepus.app.template://edu_todo")
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
    sql"insert into todo_task_category (task_id, category_id) values (${data.taskId}, ${data.categoryId})"
      .update
      .withUniqueGeneratedKeys[Long]("id")
  }

  def update(data: TaskCategory): IO[Int] = Action.transact {
    insert[TaskCategory](data)
  }

  def deleteByTaskId(taskId: Long): IO[Int] = Action.transact {
    sql"delete from todo_task_category where task_id = $taskId"
      .update
      .run
  }

  def deleteByCategoryId(categoryId: Long): IO[Int] = Action.transact {
    sql"delete from todo_task_category where category_id = $categoryId"
      .update
      .run
  }
