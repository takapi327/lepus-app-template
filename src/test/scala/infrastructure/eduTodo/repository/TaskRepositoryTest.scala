package infrastructure.eduTodo.repository

import cats.data.NonEmptyList

import cats.effect.IO
import cats.effect.unsafe.implicits.global

import org.specs2.mutable.Specification

import lepus.database.{ DataSource, DatabaseConfig }
import lepus.doobie.*
import lepus.doobie.specs2.*

import infrastructure.eduTodo.EduTodo
import infrastructure.eduTodo.model.Task

class TaskRepositoryTest extends SQLSpecification:

  def dataSource: DataSource = DataSource("lepus.database", "edu_todo", "master")

  val taskRepository = new TaskRepository

  "TaskRepository SQL Test" should {

    "Check findAll sql format" in {
      check(taskRepository.findAllQuery)
    }

    "Check get sql format" in {
      checkOutput(taskRepository.getQuery(1L))
    }

    "Check add sql format" in {
      checkOutput(taskRepository.addQuery(Task(None, "Task", None, Task.Status.TODO)))
    }

    "Check update sql format" in {
      checkOutput(taskRepository.updateQuery(Task(Some(1L), "Task", None, Task.Status.TODO)))
    }

    "Check delete sql format" in {
      checkOutput(taskRepository.deleteQuery(1L))
    }
  }

class TaskRepositoryDBAccessTest extends Specification, DBAccessSpecification[IO]:

  val database: DatabaseConfig = DatabaseConfig("lepus.database://edu_todo", NonEmptyList.of("master", "slave"))

  val taskRepository = new TaskRepository

  "TaskRepository Test" should {

    "Check findAll database access" in {
      val result = taskRepository.findAll().rollbackTransact("slave").unsafeRunSync()
      result.length === 3
    }

    "Check get database access" in {
      val result: Option[Task] = taskRepository.get(1L).rollbackTransact("slave").unsafeRunSync()
      result.nonEmpty
    }

    "Check add sql format" in {
      val task = Task(None, "new Task", None, Task.Status.TODO)
      val result = taskRepository.add(task).rollbackTransact("master").unsafeRunSync()
      result > 0
    }

    "Check update database access" in {
      val task = Task(Some(1), "Task 1 Updated", None, Task.Status.TODO)
      val result = taskRepository.update(task).rollbackTransact("master").unsafeRunSync()
      result === 1
    }

    "Check delete database access" in {
      val result = taskRepository.delete(1L).rollbackTransact("slave").unsafeRunSync()
      result === 1
    }
  }
