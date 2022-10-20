package application

import cats.effect.IO

import lepus.database.Transact

import infrastructure.databases.eduTodo

package object service:
  val taskService: Transact[IO, TaskService] = TaskService(eduTodo.taskRepository, eduTodo.categoryRepository, eduTodo.taskCategoryRepository)
  val categoryService: Transact[IO, CategoryService] = CategoryService(eduTodo.categoryRepository)
