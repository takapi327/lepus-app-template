package server

import cats.data.NonEmptyList

import cats.effect.IO

import org.http4s.dsl.io.*

import lepus.router.{ *, given }
import lepus.server.LepusApp

object HttpApp extends LepusApp[IO]:
  override def routes = NonEmptyList.of(
    "hello" / bindPath[String]("name") ->> RouterConstructor.of {
      case GET => Ok(s"hello ${summon[String]}")
    }
  )
