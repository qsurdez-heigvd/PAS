val scala3Version = "3.8.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "Bot-tender",
    version := "2026.02",
    scalaVersion := scala3Version,
    libraryDependencies ++= List(
      "org.scalameta" %% "munit" % "1.2.2" % Test,
      "org.scalameta" %% "munit-scalacheck" % "1.2.0" % Test,
    ),
    fork := true, // Required if you want to use sbt run
    libraryDependencies ++= List(
      "com.lihaoyi" %% "scalatags" % "0.13.1",
      "com.lihaoyi" %% "cask" % "0.11.3",
    ),
  )
