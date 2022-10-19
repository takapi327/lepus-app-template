package infrastructure.eduTodo.repository

import cats.data.NonEmptyList

import cats.effect.IO

import doobie.util.fragments.in

import lepus.database.*
import lepus.database.implicits.*
import lepus.logger.given

import app.model.Category

import infrastructure.eduTodo.EduTodo

case class CategoryRepository(database: EduTodo) extends DoobieRepository[IO, EduTodo](database), DoobieQueryHelper, CustomMapping:

  override val table = "todo_category"

  def findAll(): IO[List[Category]] = RunDB.use("slave") {
    select[Category].query.to[List]
  }

  def get(id: Long): IO[Option[Category]] = RunDB.use("slave") {
    select[Category].where(fr"id = $id").query.option
  }

  def filterByIds(ids: NonEmptyList[Long]): IO[Seq[Category]] = RunDB.use("slave") {
    select[Category].where(in(fr"id", ids))
      .query.to[Seq]
  }

  def add(data: Category): IO[Long] = RunDB.use("master") {
    insert[Category].values(fr"${data.id}, ${data.name}, ${data.slug}, ${data.color.toHexString}")
      .update
      .withUniqueGeneratedKeys[Long]("id")
  }

  def update(data: Category): IO[Int] = RunDB.use("master") {
    update[Category](fr"name=${data.name}, slug=${data.slug}, color=${data.color.toHexString}")
      .where(fr"id=${data.id}")
      .updateRun
  }

  def delete(id: Long): IO[Int] = RunDB.use("master") {
    delete[Category].where(fr"id = $id").updateRun
  }
