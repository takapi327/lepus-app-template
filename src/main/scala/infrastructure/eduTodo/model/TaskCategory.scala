package infrastructure.eduTodo.model

import lepus.core.generic.Schema
import lepus.core.generic.semiauto.*

case class TaskCategory(
  id:         Option[Long] = None,
  taskId:     Long,
  categoryId: Long
)

object TaskCategory:

  given Schema[TaskCategory] = deriveSchemer

/** Create new object */
  def create(taskId: Long, categoryId: Long): TaskCategory =
    TaskCategory(None, taskId, categoryId)
