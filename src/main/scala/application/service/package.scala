package application

import cats.effect.IO

import lepus.database.Transact

import infrastructure.databases.eduTodo.{ *, given }

package object service:

  val taskService: Transact[IO, TaskService] = TaskService(taskRepository, categoryRepository, taskCategoryRepository)
  val categoryService: Transact[IO, CategoryService] = CategoryService(categoryRepository)
