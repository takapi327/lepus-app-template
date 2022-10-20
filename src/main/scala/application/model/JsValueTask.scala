package application.model

import io.circe.Encoder
import io.circe.generic.semiauto.*

import infrastructure.eduTodo.model.{Category, Task, TaskCategory}

case class JsValueTask(
  id:          Option[Long],
  title:       String,
  description: Option[String],
  state:       Task.Status,
  category:    Option[JsValueCategory] = None,
)

object JsValueTask:
  given Encoder[Task.Status] = { e => Encoder.encodeShort(e.code) }
  given Encoder[JsValueTask] = deriveEncoder

  def buildMulti(
    taskSeq:         Seq[Task],
    taskCategorySeq: Seq[TaskCategory],
    categorySeq:     Seq[Category]
  ): Seq[JsValueTask] =
    for
      task            <- taskSeq
      taskCategoryOpt  = taskCategorySeq.find(_.taskId == task.id.get)
      categoryOpt      = taskCategoryOpt.flatMap(v => categorySeq.find(_.id.get == v.categoryId))
    yield
      JsValueTask(
        id          = task.id,
        title       = task.title,
        description = task.description,
        state       = task.state,
        category    = categoryOpt.map(JsValueCategory.build)
      )
