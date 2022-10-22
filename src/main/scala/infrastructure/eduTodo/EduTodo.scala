package infrastructure.eduTodo

import cats.effect.IO

import cats.data.NonEmptyList

import doobie.Transactor

import lepus.database.{ DatabaseConfig, DBTransactor, Transact, DatabaseModule }

import infrastructure.eduTodo.repository.{ CategoryRepository, TaskCategoryRepository, TaskRepository }

case class EduTodo(database: DatabaseConfig, defaultDB: String)(using DBTransactor[IO]) extends DatabaseModule[IO]

object EduTodo:
  val db: DatabaseConfig = DatabaseConfig("lepus.database://edu_todo", NonEmptyList.of("master", "slave"))

  given Transact[IO, EduTodo] = EduTodo(db, "slave")

  val taskRepository:         Transact[IO, TaskRepository]         = TaskRepository()
  val categoryRepository:     Transact[IO, CategoryRepository]     = CategoryRepository()
  val taskCategoryRepository: Transact[IO, TaskCategoryRepository] = TaskCategoryRepository()
