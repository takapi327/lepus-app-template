package app.repository

import cats.effect.IO

import doobie.implicits.*

import lepus.logger.given
import lepus.database.{ DatabaseConfig, DoobieRepository, DBTransactor, DoobieQueryHelper }

import app.model.Category

class CategoryRepository(using DBTransactor[IO]) extends DoobieRepository[IO], DoobieQueryHelper, CustomMapping:

  override val database = DatabaseConfig("lepus.app.template://edu_todo")
  override val table = "todo_category"

  def findAll(): IO[List[Category]] = Action.transact {
    select[Category].query[Category].to[List]
  }

  def get(id: Long): IO[Option[Category]] = Action.transact {
    select[Category].where(fr"id = $id").query[Category].option
  }

  def filterByIds(ids: Seq[Long]): IO[Seq[Category]] = Action.transact {
    select[Category].where(fr"id IN(${ids.mkString(",")})")
      .query[Category].to[Seq]
  }

  def add(data: Category): IO[Long] = Action.transact {
    sql"insert into todo_category (name, slug, color) values (${data.name}, ${data.slug}, ${data.color})"
      .update
      .withUniqueGeneratedKeys[Long]("id")
  }

  def update(data: Category): IO[Int] = Action.transact {
    insert[Category](data)
  }

  def delete(id: Long): IO[Int] = Action.transact {
    sql"delete from todo_category where id = $id"
      .update
      .run
  }
