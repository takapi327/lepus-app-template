ThisBuild / version      := "1.0.0"
ThisBuild / organization := "com.github.takapi327"
ThisBuild / scalaVersion := "3.2.0"
ThisBuild / startYear    := Some(2022)

ThisBuild / resolvers += "Lepus Maven" at "s3://com.github.takapi327.s3-ap-northeast-1.amazonaws.com/lepus/"

lazy val commonSettings = Seq(
  run / fork := true,

  javaOptions ++= Seq("-Dconfig.file=conf/env.dev/application.conf"),

  scalacOptions ++= Seq(
    "-Xfatal-warnings",
    "-feature",
    "utf8",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions"
  )
)

lazy val root = (project in file("."))
  .settings(name := "lepus-app-template")
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= Seq(
    "org.http4s" %% "http4s-circe" % "0.23.16",
    "mysql" % "mysql-connector-java" % "8.0.30",
    "org.specs2" %% "specs2-core" % "5.0.7"
  ))
  .enablePlugins(Lepus)
