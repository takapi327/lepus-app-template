ThisBuild / version      := "1.0.0"
ThisBuild / organization := "com.github.takapi327"
ThisBuild / scalaVersion := "3.2.0"
ThisBuild / startYear    := Some(2022)

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
  .enablePlugins(Lepus)
