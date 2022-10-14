package app.repository

import doobie.Meta

import app.model.{ Task, Category }

trait CustomMapping:

  given Meta[Task.Status] = Meta[Short].imap(v => Task.Status.fromOrdinal(v))(v => v.code)
  given Meta[Category.Color] = Meta[String].imap(v => Category.Color(v))(v => v.toString)
