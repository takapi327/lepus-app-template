package server

import cats.data.NonEmptyList

import cats.effect.IO

import org.http4s.*
import org.http4s.dsl.io.*

import lepus.router.{ *, given }
import lepus.server.LepusApp
import lepus.database.{ DatabaseConfig, DBTransactor }

import app.repository.{ TaskRepository, CategoryRepository, TaskCategoryRepository }
import app.service.{ TaskService, CategoryService }
import app.controller.api.{ TaskController, CategoryController }

type Transact[F[_], T] = DBTransactor[F] ?=> T

val id = bindPath[Long]("id")

object HttpApp extends LepusApp[IO]:

  override val databases = Set(APP.eduTodo)

  override def routes = NonEmptyList.of(
    "tasks" ->> RouterConstructor.of {
      case GET  => APP.taskController.get
      case POST => APP.taskController.post(summon[Request[IO]])
    },
    "tasks" / id ->> RouterConstructor.of {
      case GET    => APP.taskController.getById(summon[Long])
      case PUT    => APP.taskController.put(summon[Long], summon[Request[IO]])
      case DELETE => APP.taskController.delete(summon[Long])
    },
    "categories" ->> RouterConstructor.of {
      case GET  => APP.categoryController.get
      case POST => APP.categoryController.post(summon[Request[IO]])
    },
    "categories" / id ->> RouterConstructor.of {
      case PUT    => APP.categoryController.put(summon[Long], summon[Request[IO]])
      case DELETE => APP.categoryController.delete(summon[Long])
    }
  )

object APP:

  val eduTodo: DatabaseConfig = DatabaseConfig("lepus.database://edu_todo")

  val categoryRepository: Transact[IO, CategoryRepository] = CategoryRepository(eduTodo)

  val taskService: Transact[IO, TaskService] = new TaskService(TaskRepository(eduTodo), categoryRepository, TaskCategoryRepository(eduTodo))
  val categoryService: Transact[IO, CategoryService] = new CategoryService(categoryRepository)

  val taskController: Transact[IO, TaskController] = new TaskController(taskService)
  val categoryController: Transact[IO, CategoryController] = new CategoryController(categoryService)
