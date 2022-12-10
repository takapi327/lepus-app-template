package server

import scala.util.Try

import com.google.inject.Injector

import cats.syntax.all.*

import cats.effect.IO

import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.server.Router

import lepus.app.LepusApp
import lepus.logger.{ LoggerF, LoggingIO, given }

import presentation.controller.*

object LongVar {
  def unapply(str: String): Option[Long] =
    if str.nonEmpty then Try(str.toLong).toOption else None
}

object HttpApp extends LepusApp[IO], LoggingIO:

  private val taskRoutes: Injector ?=> HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ withMethod ->> Root / "tasks" => withMethod {
      case GET  => TaskController().get
      case POST => TaskController().post(using req)
    }
    case req @ withMethod ->> Root / "tasks" / LongVar(id) => withMethod {
      case GET    => TaskController().getById(id)
      case PUT    => TaskController().put(id)(using req)
      case DELETE => TaskController().delete(id)
    }
  }

  private val categoryRoutes: Injector ?=>  HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ withMethod ->> Root / "categories" => withMethod {
      case GET  => CategoryController().get
      case POST => CategoryController().post(using req)
    }
    case req @ withMethod ->> Root / "categories" / LongVar(id) => withMethod {
      case PUT    => CategoryController().put(id)(using req)
      case DELETE => CategoryController().delete(id)
    }
  }

  override val router = Router(
    "/" -> (taskRoutes <+> categoryRoutes)
  ).orNotFound

  override val errorHandler: PartialFunction[Throwable, IO[Response[IO]]] =
    case error: Throwable => logger.error(s"Unexpected error: $error", error)
    .as(Response(Status.InternalServerError))
