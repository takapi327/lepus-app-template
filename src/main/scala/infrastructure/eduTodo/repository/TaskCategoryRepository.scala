package infrastructure.eduTodo.repository

import cats.data.NonEmptyList

import cats.effect.IO

import doobie.util.fragments.in

import lepus.database.*
import lepus.database.implicits.*
import lepus.logger.given

import infrastructure.eduTodo.model.TaskCategory

class TaskCategoryRepository extends DoobieQueryHelper, DoobieLogHandler, CustomMapping:

  override val table = "todo_task_category"

  def get(id: Long): ConnectionIO[Option[TaskCategory]] =
    select[TaskCategory].where(fr"id = $id").query.option

  def findByTaskId(taskId: Long): ConnectionIO[Option[TaskCategory]] =
    select[TaskCategory].where(fr"task_id = $taskId").query.option

  def filterByTaskIds(taskIds: NonEmptyList[Long]): ConnectionIO[List[TaskCategory]] =
    select[TaskCategory].where(in(fr"task_id", taskIds)).query.to[List]

  def filterByCategoryId(categoryId: Long): ConnectionIO[Seq[TaskCategory]] =
    select[TaskCategory].where(fr"category_id = $categoryId").query.to[Seq]

  def add(data: TaskCategory): ConnectionIO[Long] =
    insert[TaskCategory].values(fr"${data.id}", fr"${data.taskId}", fr"${data.categoryId}")
      .update
      .withUniqueGeneratedKeys[Long]("id")

  def update(data: TaskCategory): ConnectionIO[Int] =
    update[TaskCategory](fr"task_id=${data.taskId}, category_id=${data.categoryId}")
      .where(fr"id=${data.id}")
      .updateRun

  def deleteByTaskId(taskId: Long): ConnectionIO[Int] =
    delete[TaskCategory].where(fr"task_id = $taskId").updateRun

  def deleteByCategoryId(categoryId: Long): ConnectionIO[Int] =
    delete[TaskCategory].where(fr"category_id = $categoryId").updateRun
