package presentation

import cats.effect.IO

import lepus.database.Transact

import application.service.*

package object controller:
  val taskController: Transact[IO, TaskController] = TaskController(taskService)
  val categoryController: Transact[IO, CategoryController] = CategoryController(categoryService)
