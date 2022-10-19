package infrastructure.eduTodo.repository

import cats.effect.IO

import lepus.database.*
import lepus.database.implicits.*
import lepus.logger.given

import app.model.Task

import infrastructure.eduTodo.EduTodo

case class TaskRepository(database: EduTodo) extends DoobieRepository[IO, EduTodo](database), DoobieQueryHelper, CustomMapping:

  override val table = "todo_task"

  def findAll(): IO[List[Task]] = RunDB {
    select[Task].query.to[List]
  }

  def get(id: Long): IO[Option[Task]] = RunDB {
    select[Task].where(fr"id = $id").query.option
  }

  def add(data: Task): IO[Long] = RunDB {
    insert[Task].values(fr"${data.id}", fr"${data.title}", fr"${data.description}", fr"${data.state}")
      .update
      .withUniqueGeneratedKeys[Long]("id")
  }

  def update(data: Task): IO[Int] = RunDB {
    update[Task](fr"title=${data.title}, description=${data.description}, state=${data.state}")
      .where(fr"id=${data.id}")
      .updateRun
  }

  def delete(id: Long): IO[Int] = RunDB {
    delete[Task].where(fr"id = $id").updateRun
  }
