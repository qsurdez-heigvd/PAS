val scala3Version = "3.8.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala3",
    version := "0.1.1-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "1.2.2" % Test,
      "org.scalameta" %% "munit-scalacheck" % "1.2.0" % Test,
    ),
  )
