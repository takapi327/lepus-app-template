package infrastructure.eduTodo.repository

import javax.inject.Singleton

import cats.effect.IO

import lepus.doobie.*
import lepus.doobie.implicits.*
import lepus.logger.given

import infrastructure.eduTodo.model.Task

@Singleton
class TaskRepository extends DoobieQueryHelper, DoobieLogHandler, CustomMapping:

  override val table = "todo_task"

  def findAll(): ConnectionIO[List[Task]] =
    findAllQuery.to[List]

  def get(id: Long): ConnectionIO[Option[Task]] =
    getQuery(id).option

  def add(data: Task): ConnectionIO[Long] =
    addQuery(data).withUniqueGeneratedKeys[Long]("id")

  def update(data: Task): ConnectionIO[Int] =
    updateQuery(data).run

  def delete(id: Long): ConnectionIO[Int] =
    deleteQuery(id).run

  val getQuery: Long => Query0[Task] = (id: Long) =>
    select[Task].where(fr"id = $id").query

  val findAllQuery: Query0[Task] =
    select[Task].query

  val addQuery: Task => Update0 = (data: Task) =>
    insert[Task].values(fr"${data.id}", fr"${data.title}", fr"${data.description}", fr"${data.state}")
      .update

  val updateQuery: Task => Update0 = (data: Task) =>
    update[Task](fr"title=${data.title}, description=${data.description}, state=${data.state}")
      .where(fr"id=${data.id}")
      .update

  val deleteQuery: Long => Update0 = (id: Long) =>
    delete[Task].where(fr"id = $id").update
