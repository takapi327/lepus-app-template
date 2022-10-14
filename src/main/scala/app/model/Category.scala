package app.model

import java.awt.Color as JavaColor

import lepus.core.generic.{ Schema, SchemaType }
import lepus.core.generic.semiauto.*

case class Category(
  id:        Option[Long]    = None,
  name:      String,
  slug:      String,
  color:     Category.Color,
)

object Category:

  given Schema[JavaColor] = Schema(SchemaType.SString())
  given Schema[Category]  = deriveSchemer

  opaque type Color = JavaColor
  object Color:
    def apply(color: JavaColor): Color = color
    def apply(colorStr: String): Color = JavaColor.decode(colorStr)

  extension (color: Color)
    def unlift: JavaColor = color

    /** Get color code as hex string */
    def toHexString: String =
      "#%s%s%s".format(
        unlift.getRed.toHexString,
        unlift.getGreen.toHexString,
        unlift.getBlue.toHexString
      )
