ThisBuild / scalaVersion := "2.13.6"
ThisBuild / organization := "com.spandigital"
ThisBuild / version := "0.1"
val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.6"

lazy val hello = (project in file("."))
  .settings(
    name := "httpsrv",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
      "com.typesafe.slick" %% "slick" % "3.3.3",
      "org.postgresql" % "postgresql" % "42.2.23",
      "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion,
      "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion,
      "org.scalactic" %% "scalactic" % "3.2.9",
      "org.scalatest" %% "scalatest" % "3.2.9" % "test"
    )
  )
