package presentation.controller.api

import cats.effect.IO

import io.circe.syntax.*

import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityDecoder.*
import application.service.CategoryService
import presentation.model.{JsValueCategory, JsValuePostCategory, JsValuePutCategory}

class CategoryController(categoryService: CategoryService):

  def get: IO[Response[IO]] =
    for
      categories <- categoryService.getAll
      res        <- Ok(categories.asJson)
    yield res

  def post(using request: Request[IO]): IO[Response[IO]] =
    given EntityDecoder[IO, JsValuePostCategory] = circeEntityDecoder[IO, JsValuePostCategory]
    for
      post <- request.as[JsValuePostCategory]
      _    <- categoryService.add(post)
      res  <- Ok("Success")
    yield res

  def put(using id: Long)(using request: Request[IO]): IO[Response[IO]] =
    given EntityDecoder[IO, JsValuePutCategory] = circeEntityDecoder[IO, JsValuePutCategory]
    for
      put  <- request.as[JsValuePutCategory]
      _    <- categoryService.update(id, put).value
      res  <- Ok("Success")
    yield res

  def delete(using id: Long): IO[Response[IO]] = categoryService.delete(id) >> Ok("Success")
