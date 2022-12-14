package presentation.controller

import javax.inject.{ Inject, Singleton }

import com.google.inject.Injector

import cats.effect.IO

import io.circe.syntax.*

import org.http4s.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.dsl.io.*

import lepus.guice.inject.Inject as LepusInject

import application.model.{ JsValuePostTask, JsValuePutTask }
import application.service.TaskService

@Singleton
class TaskController @Inject()(taskService: TaskService):
  def get: IO[Response[IO]] =
    for
      tasks <- taskService.getAll
      res   <- Ok(tasks.asJson)
    yield res

  def post(using request: Request[IO]): IO[Response[IO]] =
    given EntityDecoder[IO, JsValuePostTask] = circeEntityDecoder[IO, JsValuePostTask]
    for
      post <- request.as[JsValuePostTask]
      _    <- taskService.add(post)
      res  <- Ok("Success")
    yield res

  def getById(id: Long): IO[Response[IO]] =
    for
      taskOpt <- taskService.get(id)
      res     <- taskOpt match
        case Some(task) => Ok(task.asJson)
        case None       => NotFound(s"ID: No Task matching $id exists")
    yield res

  def put(id: Long)(using request: Request[IO]): IO[Response[IO]] =
    given EntityDecoder[IO, JsValuePutTask] = circeEntityDecoder[IO, JsValuePutTask]
    for
      put <- request.as[JsValuePutTask]
      _   <- taskService.update(id, put).value
      res <- Ok("Success")
    yield res

  def delete(id: Long): IO[Response[IO]] = taskService.delete(id) >> Ok("Success")

object TaskController:
  def apply(): Injector ?=> TaskController = LepusInject[TaskController]
