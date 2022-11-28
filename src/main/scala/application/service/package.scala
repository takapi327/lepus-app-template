package application

import cats.effect.IO

import lepus.doobie.Transact

import infrastructure.databases.eduTodo.{ *, given }

package object service:

  val taskService: Transact[TaskService] = TaskService(taskRepository, categoryRepository, taskCategoryRepository)
  val categoryService: Transact[CategoryService] = CategoryService(categoryRepository)
