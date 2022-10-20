package presentation.model

import io.circe.Decoder
import io.circe.generic.semiauto.*

import infrastructure.eduTodo.model.Category

case class JsValuePutCategory(
  name:  String,
  slug:  String,
  color: Category.Color,
)

object JsValuePutCategory:
  given Decoder[JsValuePutCategory] = Decoder.instance { d =>
    for
      name  <- d.downField("name").as[String]
      slug  <- d.downField("slug").as[String]
      color <- d.downField("color").as[String]
    yield JsValuePutCategory(name, slug, Category.Color(color))
  }
