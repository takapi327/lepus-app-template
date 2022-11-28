package server

import cats.data.NonEmptyList

import cats.effect.IO

import org.http4s.*
import org.http4s.dsl.io.*

import lepus.router.{ *, given }
import lepus.server.LepusApp
import lepus.logger.{ LoggerF, LoggingIO, given }

import infrastructure.databases.eduTodo

import presentation.controller.*

val id = bindPath[Long]("id")

object HttpApp extends LepusApp[IO], LoggingIO:

  given LoggerF[IO] = logger

  override val databases = Set(eduTodo.db)

  override val routes = NonEmptyList.of(
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

  override val errorHandler: PartialFunction[Throwable, IO[Response[IO]]] =
    case error: Throwable => logger.error(s"Unexpected error: $error", error)
    .as(Response(Status.InternalServerError))
