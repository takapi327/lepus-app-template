package app.service

import cats.effect.IO

import cats.data.EitherT

import app.model.Category
import app.model.json.{ JsValueCategory, JsValuePostCategory, JsValuePutCategory }
import infrastructure.eduTodo.repository.CategoryRepository

class CategoryService(
  categoryRepository: CategoryRepository
):
  def getAll: IO[Seq[JsValueCategory]] =
    for categories <- categoryRepository.findAll()
    yield categories.map(JsValueCategory.build)

  def add(category: JsValuePostCategory): IO[Long] =
    categoryRepository.add(Category(None, category.name, category.slug, category.color))

  def update(id: Long, params: JsValuePutCategory): EitherT[IO, Throwable, Int] =
    EitherT.fromOptionF(categoryRepository.get(id), IllegalArgumentException("")) semiflatMap { category =>
      categoryRepository.update(category.copy(
        name  = params.name,
        slug  = params.slug,
        color = params.color
      ))
    }

  def delete(id: Long): IO[Int] = categoryRepository.delete(id)
