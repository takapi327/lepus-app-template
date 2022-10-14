ThisBuild / version      := "1.0.0"
ThisBuild / organization := "com.github.takapi327"
ThisBuild / scalaVersion := "3.2.0"
ThisBuild / startYear    := Some(2022)

lazy val root = (project in file("."))
  .settings(name := "lepus-app-template")
  .settings(
    run / fork := true,

    javaOptions ++= Seq("-Dconfig.file=conf/env.dev/application.conf"),

    scalaOptions ++= Seq(
      "-Xfatal-warnings",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-encoding",
      "utf8",
      "-language:existentials",
      "-language:higherKinds",
      "-language:implicitConversions"
    )
  )

