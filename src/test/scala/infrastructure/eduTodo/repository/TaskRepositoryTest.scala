package infrastructure.eduTodo.repository

import cats.data.NonEmptyList

import cats.effect.IO
import cats.effect.unsafe.implicits.global

import org.specs2.mutable.Specification

import lepus.database.DatabaseConfig
import lepus.database.specs2.SpecDatabaseBuilder

import infrastructure.eduTodo.EduTodo

class TaskRepositoryTest extends Specification, SpecDatabaseBuilder[IO]:

  val database: DatabaseConfig = DatabaseConfig("lepus.database://edu_todo", NonEmptyList.of("master", "slave"))

  given EduTodo = EduTodo(database, "slave")

  "TaskRepository Test" should {
    "findAll" in {
      val result = TaskRepository().findAll().unsafeRunSync()
      result.length === 3
    }
  }
