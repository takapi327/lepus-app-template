package app.model.json

import io.circe.generic.semiauto.*
import io.circe.{ Decoder, Encoder }

import app.model.Task

case class JsValuePutTask(
  title:       String,
  description: Option[String],
  state:       Task.Status,
  categoryId:  Option[Long],
)

object JsValuePutTask:
  given Decoder[JsValuePutTask] = Decoder.instance { d =>
    for
      title       <- d.downField("title").as[String]
      description <- d.downField("description").as[Option[String]]
      state       <- d.downField("state").as[Short]
      categoryId  <- d.downField("category_id").as[Option[Long]]
    yield JsValuePutTask(title, description, Task.Status.values.find(_.code == state).get, categoryId)
  }
