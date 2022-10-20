package application.model

import io.circe.Decoder
import io.circe.generic.semiauto.*

import infrastructure.eduTodo.model.Category

case class JsValuePostCategory(
  name:  String,
  slug:  String,
  color: Category.Color,
)

object JsValuePostCategory:
  given Decoder[JsValuePostCategory] = Decoder.instance { d =>
    for
      name  <- d.downField("name").as[String]
      slug  <- d.downField("slug").as[String]
      color <- d.downField("color").as[String]
    yield JsValuePostCategory(name, slug, Category.Color(color))
  }
