package app

import cats.effect.IO

import lepus.database.Transact

import app.service.*

package object controller:
  val taskController: Transact[IO, api.TaskController] = api.TaskController(taskService)
  val categoryController: Transact[IO, api.CategoryController] = api.CategoryController(categoryService)
