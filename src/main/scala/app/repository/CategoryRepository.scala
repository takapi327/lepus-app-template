package app.repository

import cats.effect.IO

import doobie.implicits.*

import lepus.logger.given
import lepus.database.{ DatabaseConfig, DoobieRepository, DBTransactor, DoobieQueryHelper }

import app.model.Category

case class CategoryRepository(
  database: DatabaseConfig
)(using DBTransactor[IO]) extends DoobieRepository[IO], DoobieQueryHelper, CustomMapping:

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
    insert[Category].values(fr"${data.id}", fr"${data.name}", fr"${data.slug}", fr"${data.color.toHexString}")
      .update
      .withUniqueGeneratedKeys[Long]("id")
  }

  def update(data: Category): IO[Int] = Action.transact {
    update.set(fr"name=${data.name}, slug=${data.slug}, color=${data.color.toHexString}")
      .where(fr"id=${data.id}")
      .updateRun
  }

  def delete(id: Long): IO[Int] = Action.transact {
    delete.where(fr"id = $id").updateRun
  }
