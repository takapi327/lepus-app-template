package server

import cats.data.NonEmptyList

import cats.effect.IO

import org.http4s.*
import org.http4s.dsl.io.*

import lepus.router.{ *, given }
import lepus.server.LepusApp
import lepus.logger.{ LoggerF, LoggingIO, given }

import presentation.controller.*

val id = bindPath[Long]("id")

object HttpApp extends LepusApp[IO], LoggingIO:

  given LoggerF[IO] = logger

  override val routes = NonEmptyList.of(
    "tasks" ->> RouterConstructor.of {
      case GET  => TaskController().get
      case POST => TaskController().post
    },
    "tasks" / id ->> RouterConstructor.of {
      case GET    => TaskController().getById
      case PUT    => TaskController().put
      case DELETE => TaskController().delete
    },
    "categories" ->> RouterConstructor.of {
      case GET  => CategoryController().get
      case POST => CategoryController().post
    },
    "categories" / id ->> RouterConstructor.of {
      case PUT    => CategoryController().put
      case DELETE => CategoryController().delete
    }
  )

  override val errorHandler: PartialFunction[Throwable, IO[Response[IO]]] =
    case error: Throwable => logger.error(s"Unexpected error: $error", error)
    .as(Response(Status.InternalServerError))
