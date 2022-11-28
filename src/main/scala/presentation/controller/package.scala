package presentation

import cats.effect.IO

import lepus.doobie.Transact

import application.service.*

package object controller:
  val taskController: Transact[TaskController] = TaskController(taskService)
  val categoryController: Transact[CategoryController] = CategoryController(categoryService)
