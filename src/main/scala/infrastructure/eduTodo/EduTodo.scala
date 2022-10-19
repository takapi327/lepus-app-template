package infrastructure.eduTodo

import cats.effect.IO

import doobie.Transactor

import lepus.database.{ DatabaseConfig, DBTransactor, Transact, DatabaseModule }

import infrastructure.eduTodo.repository.{ CategoryRepository, TaskCategoryRepository, TaskRepository }

case class EduTodo(database: DatabaseConfig)(using DBTransactor[IO]) extends DatabaseModule[IO]

object EduTodo:
  val db: DatabaseConfig = DatabaseConfig("lepus.database://edu_todo", Seq("master", "slave"))

  val value: Transact[IO, infrastructure.eduTodo.EduTodo] =
    infrastructure.eduTodo.EduTodo(db)

  val taskRepository:         Transact[IO, TaskRepository]         = TaskRepository(value)
  val categoryRepository:     Transact[IO, CategoryRepository]     = CategoryRepository(value)
  val taskCategoryRepository: Transact[IO, TaskCategoryRepository] = TaskCategoryRepository(value)
