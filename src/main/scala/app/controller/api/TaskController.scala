package app.controller.api

import cats.effect.IO

import io.circe.syntax.*

import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityDecoder.*

import app.model.json.{ JsValuePostTask, JsValuePutTask }
import app.service.TaskService

class TaskController(taskService: TaskService):
  def get: IO[Response[IO]] =
    for
      tasks <- taskService.getAll
      res   <- Ok(tasks.asJson)
    yield res

  def post(request: Request[IO]): IO[Response[IO]] =
    given EntityDecoder[IO, JsValuePostTask] = circeEntityDecoder[IO, JsValuePostTask]
    for
      post <- request.as[JsValuePostTask]
      _    <- taskService.add(post)
      res  <- Ok("成功")
    yield res

  def getById(id: Long): IO[Response[IO]] =
    for
      taskOpt <- taskService.get(id)
      res     <- taskOpt match
        case Some(task) => Ok(task.asJson)
        case None       => NotFound(s"$id に一致するTaskが存在しない")
    yield res

  def put(id: Long, request: Request[IO]): IO[Response[IO]] =
    given EntityDecoder[IO, JsValuePutTask] = circeEntityDecoder[IO, JsValuePutTask]
    for
      put <- request.as[JsValuePutTask]
      _   <- taskService.update(id, put).value
      res <- Ok("成功")
    yield res

  def delete(id: Long): IO[Response[IO]] = taskService.delete(id) >> Ok("成功")
