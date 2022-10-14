package server

import cats.data.NonEmptyList

import cats.effect.IO

import org.http4s.*
import org.http4s.dsl.io.*

import lepus.router.{ *, given }
import lepus.server.LepusApp
import lepus.database.DatabaseConfig

import app.repository.{ TaskRepository, CategoryRepository, TaskCategoryRepository }
import app.service.TaskService
import app.controller.api.TaskController

object HttpApp extends LepusApp[IO]:

  override val databases = Set(
    DatabaseConfig("lepus.app.template://master/edu_todo")
  )

  override def routes =
    val taskRepository         = new TaskRepository
    val categoryRepository     = new CategoryRepository
    val taskCategoryRepository = new TaskCategoryRepository

    val taskService = new TaskService(taskRepository, taskCategoryRepository, categoryRepository)
    val taskController = new TaskController(taskService)
    NonEmptyList.of(

    "tasks" ->> RouterConstructor.of {
      case GET  => taskController.get
      case POST => taskController.post(summon[Request[IO]])
    },
    "tasks" / bindPath[Long]("id") ->> RouterConstructor.of {
      case GET    => taskController.getById(summon[Long])
      case PUT    => taskController.put(summon[Long], summon[Request[IO]])
      case DELETE => taskController.delete(summon[Long])
    },
    "categories" ->> RouterConstructor.of {
      case GET  => ???
      case POST => ???
    },
    "categories" / bindPath[Long]("id") ->> RouterConstructor.of {
      case PUT    => ???
      case DELETE => ???
    }
  )
