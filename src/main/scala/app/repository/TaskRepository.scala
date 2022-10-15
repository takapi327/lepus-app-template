package app.repository

import cats.effect.IO

import doobie.implicits.*

import lepus.logger.given
import lepus.database.{ DatabaseConfig, DoobieRepository, DBTransactor, DoobieQueryHelper }

import app.model.Task

class TaskRepository(using DBTransactor[IO]) extends DoobieRepository[IO], DoobieQueryHelper, CustomMapping:

  override def database = DatabaseConfig("lepus.app.template://master/edu_todo")
  override val table = "todo_task"

  def findAll(): IO[List[Task]] = Action.transact {
    select[Task].query[Task].to[List]
  }

  def get(id: Long): IO[Option[Task]] = Action.transact {
    select[Task].where(fr"id = $id").query[Task].option
  }

  def add(data: Task): IO[Long] = Action.transact {
    insert[Task].values(fr"${data.title}", fr"${data.description}", fr"${data.state}")
      .update
      .withUniqueGeneratedKeys[Long]("id")
  }

  def update(data: Task): IO[Int] = Action.transact {
    insert[Task](data)
  }

  def delete(id: Long): IO[Int] = Action.transact {
    delete.where(fr"id = $id").updateRun
  }
