package server

import cats.data.NonEmptyList

import cats.effect.IO

import org.http4s.*
import org.http4s.dsl.io.*

import lepus.router.{ *, given }
import lepus.server.LepusApp
import lepus.database.{ DatabaseConfig, DBTransactor }

import app.service.{ TaskService, CategoryService }
import app.controller.api.{ TaskController, CategoryController }

type Transact[F[_], T] = DBTransactor[F] ?=> T

val id = bindPath[Long]("id")

object HttpApp extends LepusApp[IO]:

  val db: DatabaseConfig = DatabaseConfig("lepus.database://edu_todo")

  val eduTodo: Transact[IO, infrastructure.eduTodo.EduTodo] =
    infrastructure.eduTodo.EduTodo(db)

  override val databases = Set(db)

  val taskService: Transact[IO, TaskService] = TaskService(eduTodo.taskRepository, eduTodo.categoryRepository, eduTodo.taskCategoryRepository)
  val categoryService: Transact[IO, CategoryService] = CategoryService(eduTodo.categoryRepository)

  val taskController: Transact[IO, TaskController] = TaskController(taskService)
  val categoryController: Transact[IO, CategoryController] = CategoryController(categoryService)

  override def routes = NonEmptyList.of(
    "tasks" ->> RouterConstructor.of {
      case GET  => taskController.get
      case POST => taskController.post
    },
    "tasks" / id ->> RouterConstructor.of {
      case GET    => taskController.getById(summon[Long])
      case PUT    => taskController.put(summon[Long])
      case DELETE => taskController.delete(summon[Long])
    },
    "categories" ->> RouterConstructor.of {
      case GET  => categoryController.get
      case POST => categoryController.post
    },
    "categories" / id ->> RouterConstructor.of {
      case PUT    => categoryController.put(summon[Long])
      case DELETE => categoryController.delete(summon[Long])
    }
  )
