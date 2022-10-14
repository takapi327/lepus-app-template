package app.repository

import cats.effect.IO

import doobie.implicits.*

import lepus.logger.given
import lepus.database.{ DatabaseConfig, DoobieRepository, DBTransactor, DoobieQueryHelper }

import app.model.Task

class TaskRepository(using DBTransactor[IO]) extends DoobieRepository[IO], DoobieQueryHelper, CustomMapping:

  override val database = DatabaseConfig("lepus.app.template://edu_todo")
  override val table = "todo_task"

  def findAll(): IO[List[Task]] = Action.transact {
    select[Task].query[Task].to[List]
  }

  def get(id: Long): IO[Option[Task]] = Action.transact {
    select[Task].where(fr"id = $id").query[Task].option
  }

  def add(data: Task): IO[Long] = Action.transact {
    sql"insert into todo_task (title, description, state) values (${data.title}, ${data.description}, ${data.state})"
      .update
      .withUniqueGeneratedKeys[Long]("id")
  }

  def update(data: Task): IO[Int] = Action.transact {
    insert[Task](data)
  }

  def delete(id: Long): IO[Int] = Action.transact {
    sql"delete from todo_task where id = $id"
      .update
      .run
  }
