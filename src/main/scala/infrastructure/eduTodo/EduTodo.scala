package infrastructure.eduTodo

import cats.effect.IO

import doobie.Transactor

import lepus.database.{ DatabaseConfig, DBTransactor, DatabaseModule }
import infrastructure.eduTodo.repository.{CategoryRepository, TaskCategoryRepository, TaskRepository}

case class EduTodo(database: DatabaseConfig)(using DBTransactor[IO]) extends DatabaseModule[IO]:

  val taskRepository         = new TaskRepository
  val categoryRepository     = new CategoryRepository
  val taskCategoryRepository = new TaskCategoryRepository
