package infrastructure.eduTodo.repository

import lepus.doobie.Meta
import infrastructure.eduTodo.model.{Category, Task}

trait CustomMapping:

  given Meta[Task.Status] = Meta[Short].imap(v => Task.Status.values.find(_.code == v).get)(v => v.code)

  given Meta[Category.Color] = Meta[String].imap(v => Category.Color(v))(v => v.toString)
