package presentation.controller.api

import application.model.{JsValuePostTask, JsValuePutTask}
import cats.effect.IO

import io.circe.syntax.*

import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityDecoder.*
import application.service.TaskService

class TaskController(taskService: TaskService):
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

  def getById(using id: Long): IO[Response[IO]] =
    for
      taskOpt <- taskService.get(id)
      res     <- taskOpt match
        case Some(task) => Ok(task.asJson)
        case None       => NotFound(s"ID: No Task matching $id exists")
    yield res

  def put(using id: Long)(using request: Request[IO]): IO[Response[IO]] =
    given EntityDecoder[IO, JsValuePutTask] = circeEntityDecoder[IO, JsValuePutTask]
    for
      put <- request.as[JsValuePutTask]
      _   <- taskService.update(id, put).value
      res <- Ok("Success")
    yield res

  def delete(using id: Long): IO[Response[IO]] = taskService.delete(id) >> Ok("Success")
