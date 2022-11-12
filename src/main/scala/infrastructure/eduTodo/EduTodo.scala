package infrastructure.eduTodo

import cats.effect.IO

import cats.data.NonEmptyList

import doobie.Transactor

import lepus.database.{ DatabaseConfig, DBTransactor, Transact, DatabaseModule }

import infrastructure.eduTodo.repository.*

case class EduTodo(database: DatabaseConfig, defaultDB: String)(using DBTransactor[IO]) extends DatabaseModule[IO]

object EduTodo:
  val db: DatabaseConfig = DatabaseConfig("lepus.database://edu_todo", NonEmptyList.of("master", "slave"))

  given Transact[IO, EduTodo] = EduTodo(db, "slave")

  val taskRepository:         Transact[IO, TaskRepository]         = new TaskRepository
  val categoryRepository:     Transact[IO, CategoryRepository]     = new CategoryRepository
  val taskCategoryRepository: Transact[IO, TaskCategoryRepository] = new TaskCategoryRepository
