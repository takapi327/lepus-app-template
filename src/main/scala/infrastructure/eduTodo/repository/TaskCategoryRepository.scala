package infrastructure.eduTodo.repository

import cats.data.NonEmptyList

import cats.effect.IO

import doobie.util.fragments.in

import lepus.database.*
import lepus.database.implicits.*
import lepus.logger.given

import app.model.*

import infrastructure.eduTodo.EduTodo

case class TaskCategoryRepository(database: EduTodo) extends DoobieRepository[IO, EduTodo](database), DoobieQueryHelper, CustomMapping:

  override val table = "todo_task_category"

  def get(id: Long): IO[Option[TaskCategory]] = RunDB {
    select[TaskCategory].where(fr"id = $id").query.option
  }

  def findByTaskId(taskId: Long): IO[Option[TaskCategory]] = RunDB {
    select[TaskCategory].where(fr"task_id = $taskId").query.option
  }

  def filterByTaskIds(taskIds: NonEmptyList[Long]): IO[List[TaskCategory]] = RunDB {
    select[TaskCategory].where(in(fr"task_id", taskIds)).query.to[List]
  }

  def filterByCategoryId(categoryId: Long): IO[Seq[TaskCategory]] = RunDB {
    select[TaskCategory].where(fr"category_id = $categoryId").query.to[Seq]
  }

  def add(data: TaskCategory): IO[Long] = RunDB {
    insert[TaskCategory].values(fr"${data.id}", fr"${data.taskId}", fr"${data.categoryId}")
      .update
      .withUniqueGeneratedKeys[Long]("id")
  }

  def update(data: TaskCategory): IO[Int] = RunDB {
    update[TaskCategory](fr"task_id=${data.taskId}, category_id=${data.categoryId}")
      .where(fr"id=${data.id}")
      .updateRun
  }

  def deleteByTaskId(taskId: Long): IO[Int] = RunDB {
    delete[TaskCategory].where(fr"task_id = $taskId").updateRun
  }

  def deleteByCategoryId(categoryId: Long): IO[Int] = RunDB {
    delete[TaskCategory].where(fr"category_id = $categoryId").updateRun
  }
