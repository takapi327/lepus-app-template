package infrastructure.eduTodo

import cats.effect.IO

import cats.data.NonEmptyList

import lepus.database.DatabaseConfig
import lepus.hikari.HikariContext
import lepus.doobie.*

import infrastructure.eduTodo.repository.*

case class EduTodo(database: DatabaseConfig, defaultDB: String)(using HikariContext) extends DatabaseModule[IO]

object EduTodo:
  val db: DatabaseConfig = DatabaseConfig("lepus.database://edu_todo", NonEmptyList.of("master", "slave"))

  given Transact[EduTodo] = EduTodo(db, "slave")

  val taskRepository         = new TaskRepository
  val categoryRepository     = new CategoryRepository
  val taskCategoryRepository = new TaskCategoryRepository
