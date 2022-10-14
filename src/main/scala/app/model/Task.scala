package app.model

import lepus.core.generic.Schema
import lepus.core.generic.semiauto.*

case class Task(
  id:          Option[Long]   = None,
  title:       String,
  description: Option[String] = None,
  state:       Task.Status    = Status.TODO,
)

object Task:

  given Schema[Task.Status] = deriveSchemer
  given Schema[Task]        = deriveSchemer

  enum Status(val code: Short):
    case TODO        extends Status(code = 1)
    case IN_PROGRESS extends Status(code = 2)
    case DONE        extends Status(code = 3)
