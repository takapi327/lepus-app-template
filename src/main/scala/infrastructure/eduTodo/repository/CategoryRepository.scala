package infrastructure.eduTodo.repository

import javax.inject.Singleton

import cats.data.NonEmptyList

import cats.effect.IO

import doobie.util.fragments.in

import lepus.doobie.*
import lepus.doobie.implicits.*
import lepus.logger.given

import infrastructure.eduTodo.model.Category

@Singleton
class CategoryRepository extends DoobieQueryHelper, DoobieLogHandler, CustomMapping:

  override val table = "todo_category"

  def findAll(): ConnectionIO[List[Category]] =
    select[Category].query.to[List]

  def get(id: Long): ConnectionIO[Option[Category]] =
    select[Category].where(fr"id = $id").query.option

  def filterByIds(ids: NonEmptyList[Long]): ConnectionIO[Seq[Category]] =
    select[Category].where(in(fr"id", ids))
      .query.to[Seq]

  def add(data: Category): ConnectionIO[Long] =
    insert[Category].values(fr"${data.id}, ${data.name}, ${data.slug}, ${data.color.toHexString}")
      .update
      .withUniqueGeneratedKeys[Long]("id")

  def update(data: Category): ConnectionIO[Int] =
    update[Category](fr"name=${data.name}, slug=${data.slug}, color=${data.color.toHexString}")
      .where(fr"id=${data.id}")
      .updateRun

  def delete(id: Long): ConnectionIO[Int] =
    delete[Category].where(fr"id = $id").updateRun
