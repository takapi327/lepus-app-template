package infrastructure.eduTodo.repository

import cats.effect.IO

import lepus.database.*
import lepus.logger.given

import app.model.Category

class CategoryRepository(using Transactor[IO]) extends DoobieRepository[IO], DoobieQueryHelper, CustomMapping:

  override val table = "todo_category"

  def findAll(): IO[List[Category]] =
    select[Category].query.to[List]

  def get(id: Long): IO[Option[Category]] =
    select[Category].where(fr"id = $id").query.option

  def filterByIds(ids: Seq[Long]): IO[Seq[Category]] =
    select[Category].where(fr"id IN(${ids.mkString(",")})")
      .query.to[Seq]

  def add(data: Category): IO[Long] =
    insert[Category].values(fr"${data.id}, ${data.name}, ${data.slug}, ${data.color.toHexString}")
      .update
      .withUniqueGeneratedKeys[Long]("id")

  def update(data: Category): IO[Int] =
    update[Category](fr"name=${data.name}, slug=${data.slug}, color=${data.color.toHexString}")
      .where(fr"id=${data.id}")
      .updateRun

  def delete(id: Long): IO[Int] =
    delete[Category].where(fr"id = $id").updateRun
