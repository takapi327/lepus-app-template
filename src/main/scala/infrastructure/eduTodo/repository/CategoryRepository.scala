package infrastructure.eduTodo.repository

import cats.data.NonEmptyList

import cats.effect.IO

import doobie.util.fragments.in

import lepus.database.*
import lepus.database.implicits.*
import lepus.logger.given

import infrastructure.eduTodo.EduTodo
import infrastructure.eduTodo.model.Category

case class CategoryRepository()(using EduTodo) extends DoobieRepository[IO, EduTodo], DoobieQueryHelper, CustomMapping:

  override val table = "todo_category"

  def findAll(): IO[List[Category]] = RunDB {
    select[Category].query.to[List]
  }

  def get(id: Long): IO[Option[Category]] = RunDB {
    select[Category].where(fr"id = $id").query.option
  }

  def filterByIds(ids: NonEmptyList[Long]): IO[Seq[Category]] = RunDB {
    select[Category].where(in(fr"id", ids))
      .query.to[Seq]
  }

  def add(data: Category): IO[Long] = RunDB("master") {
    insert[Category].values(fr"${data.id}, ${data.name}, ${data.slug}, ${data.color.toHexString}")
      .update
      .withUniqueGeneratedKeys[Long]("id")
  }

  def update(data: Category): IO[Int] = RunDB("master") {
    update[Category](fr"name=${data.name}, slug=${data.slug}, color=${data.color.toHexString}")
      .where(fr"id=${data.id}")
      .updateRun
  }

  def delete(id: Long): IO[Int] = RunDB("master") {
    delete[Category].where(fr"id = $id").updateRun
  }
