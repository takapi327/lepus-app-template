package server

import cats.data.NonEmptyList

import cats.effect.IO

import org.http4s.*
import org.http4s.dsl.io.*

import lepus.router.{ *, given }
import lepus.server.LepusApp
import lepus.database.{ DatabaseConfig, DBTransactor }

import app.repository.{ TaskRepository, CategoryRepository, TaskCategoryRepository }
import app.service.TaskService
import app.controller.api.TaskController

type Transact[F[_], T] = DBTransactor[F] ?=> T

val id = bindPath[Long]("id")

object HttpApp extends LepusApp[IO]:

  val eduTodo = DatabaseConfig("lepus.app.template://master/edu_todo")

  override val databases = Set(eduTodo)

  val taskRepository:         Transact[IO, TaskRepository]         = TaskRepository(eduTodo)
  val categoryRepository:     Transact[IO, CategoryRepository]     = CategoryRepository(eduTodo)
  val taskCategoryRepository: Transact[IO, TaskCategoryRepository] = TaskCategoryRepository(eduTodo)

  val taskService: Transact[IO, TaskService] = new TaskService(taskRepository, taskCategoryRepository, categoryRepository)

  val taskController: Transact[IO, TaskController] = new TaskController(taskService)

  override def routes = NonEmptyList.of(
    "tasks" ->> RouterConstructor.of {
      case GET  => taskController.get
      case POST => taskController.post(summon[Request[IO]])
    },
    "tasks" / id ->> RouterConstructor.of {
      case GET    => taskController.getById(summon[Long])
      case PUT    => taskController.put(summon[Long], summon[Request[IO]])
      case DELETE => taskController.delete(summon[Long])
    },
    "categories" ->> RouterConstructor.of {
      case GET  => ???
      case POST => ???
    },
    "categories" / id ->> RouterConstructor.of {
      case PUT    => ???
      case DELETE => ???
    }
  )
