package application.service

import cats.effect.IO

import cats.data.EitherT

import lepus.database.*
import lepus.database.implicits.*

import application.model.{ JsValueCategory, JsValuePostCategory, JsValuePutCategory }
import infrastructure.eduTodo.model.Category
import infrastructure.eduTodo.repository.CategoryRepository

class CategoryService(
  categoryRepository: CategoryRepository
)(using DatabaseModule[IO]):

  def getAll: IO[Seq[JsValueCategory]] =
    ((for categories <- categoryRepository.findAll()
    yield categories.map(JsValueCategory.build)): ConnectionIO[Seq[JsValueCategory]]).transaction

  def add(category: JsValuePostCategory): IO[Long] =
    categoryRepository.add(Category(None, category.name, category.slug, category.color)).transaction("master")

  def update(id: Long, params: JsValuePutCategory): EitherT[IO, Throwable, Int] =
    EitherT.fromOptionF[ConnectionIO, Throwable, Category](categoryRepository.get(id), IllegalArgumentException(s"Category with id $id does not exist")).semiflatMap(category =>
      categoryRepository.update(category.copy(
        name  = params.name,
        slug  = params.slug,
        color = params.color
      ))
    ).transaction("master")

  def delete(id: Long): IO[Int] = categoryRepository.delete(id).transaction("master")
