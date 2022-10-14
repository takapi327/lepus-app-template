package app.model.json

import io.circe.{ Encoder, Decoder }
import io.circe.generic.semiauto.*

case class JsValuePostTask(
  title:       String,
  description: Option[String],
  categoryId:  Option[Long],
)

object JsValuePostTask:
  given Encoder[JsValuePostTask] = deriveEncoder
  given Decoder[JsValuePostTask] = Decoder.instance { d =>
    for
      title       <- d.downField("title").as[String]
      description <- d.downField("description").as[Option[String]]
      categoryId  <- d.downField("category_id").as[Option[Long]]
    yield JsValuePostTask(title, description, categoryId)
  }
