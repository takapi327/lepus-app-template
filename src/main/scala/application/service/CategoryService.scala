package application.service

import javax.inject.{ Inject, Singleton }

import com.google.inject.name.Named

import cats.effect.IO

import cats.data.EitherT

import lepus.doobie.*
import lepus.doobie.implicits.*

import application.model.{ JsValueCategory, JsValuePostCategory, JsValuePutCategory }
import infrastructure.eduTodo.model.Category
import infrastructure.eduTodo.repository.CategoryRepository

@Singleton
class CategoryService @Inject()(
  @Named("edu_todo_master") master: ContextIO,
  @Named("edu_todo_slave")  slave:  ContextIO,
  categoryRepository: CategoryRepository
):

  def getAll: IO[Seq[JsValueCategory]] =
    (for categories <- categoryRepository.findAll()
    yield categories.map(JsValueCategory.build)).transact(slave.xa)

  def add(category: JsValuePostCategory): IO[Long] =
    categoryRepository.add(Category(None, category.name, category.slug, category.color)).transact(master.xa)

  def update(id: Long, params: JsValuePutCategory): EitherT[IO, Throwable, Int] =
    EitherT.fromOptionF[ConnectionIO, Throwable, Category](categoryRepository.get(id), IllegalArgumentException(s"Category with id $id does not exist")).semiflatMap(category =>
      categoryRepository.update(category.copy(
        name  = params.name,
        slug  = params.slug,
        color = params.color
      ))
    ).transact(master.xa)

  def delete(id: Long): IO[Int] = categoryRepository.delete(id).transact(master.xa)
