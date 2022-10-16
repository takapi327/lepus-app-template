package infrastructure.eduTodo.repository

import cats.effect.IO

import lepus.database.*
import lepus.logger.given

import app.model.Task

class TaskRepository(using Transactor[IO]) extends DoobieRepository[IO], DoobieQueryHelper, CustomMapping:

  override val table = "todo_task"

  def findAll(): IO[List[Task]] =
    select[Task].query[Task].to[List]

  def get(id: Long): IO[Option[Task]] =
    select[Task].where(fr"id = $id").query[Task].option

  def add(data: Task): IO[Long] =
    insert[Task].values(fr"${data.id}", fr"${data.title}", fr"${data.description}", fr"${data.state}")
      .update
      .withUniqueGeneratedKeys[Long]("id")

  def update(data: Task): IO[Int] =
    update.set(fr"title=${data.title}, description=${data.description}, state=${data.state}")
      .where(fr"id=${data.id}")
      .updateRun

  def delete(id: Long): IO[Int] =
    delete.where(fr"id = $id").updateRun
