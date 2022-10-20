package server

import cats.data.NonEmptyList

import cats.effect.IO

import org.http4s.*
import org.http4s.dsl.io.*

import lepus.router.{ *, given }
import lepus.server.LepusApp
import lepus.database.Transact

import infrastructure.databases.eduTodo

import presentation.controller.*

val id = bindPath[Long]("id")

object HttpApp extends LepusApp[IO]:

  override val databases = Set(eduTodo.db)

  override def routes = NonEmptyList.of(
    "tasks" ->> RouterConstructor.of {
      case GET  => taskController.get
      case POST => taskController.post
    },
    "tasks" / id ->> RouterConstructor.of {
      case GET    => taskController.getById
      case PUT    => taskController.put
      case DELETE => taskController.delete
    },
    "categories" ->> RouterConstructor.of {
      case GET  => categoryController.get
      case POST => categoryController.post
    },
    "categories" / id ->> RouterConstructor.of {
      case PUT    => categoryController.put
      case DELETE => categoryController.delete
    }
  )
