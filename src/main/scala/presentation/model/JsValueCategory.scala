package presentation.model

import io.circe.Encoder
import io.circe.generic.semiauto.*

import infrastructure.eduTodo.model.Category

case class JsValueCategory(
  id:    Option[Long],
  name:  String,
  slug:  String,
  color: Category.Color,
)

object JsValueCategory:
  given Encoder[Category.Color]  = { e => Encoder.encodeString(e.toHexString) }
  given Encoder[JsValueCategory] = deriveEncoder

  def build(data: Category): JsValueCategory =
    JsValueCategory(
      id    = data.id,
      name  = data.name,
      slug  = data.slug,
      color = data.color,
    )
