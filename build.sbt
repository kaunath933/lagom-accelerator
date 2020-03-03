import sbt.Keys._
import sbt._

name := "lagom-acclerator"

version in ThisBuild := "1.0.0"

scalaVersion in ThisBuild := "2.12.8"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test

lazy val `lagom-acclerator` = (project in file("."))
  .aggregate(`common-lagom`, `lagom-api`, `lagom-impl`)

val commonLagomAPISettings = libraryDependencies ++= Seq(
  lagomScaladslApi
)


val commonLagomImplSettings = libraryDependencies ++= Seq(
  lagomScaladslPersistenceCassandra,
  lagomScaladslKafkaBroker,
  lagomScaladslTestKit,
  macwire,
  scalaTest
)

lazy val `common-lagom` = (project in file("modules/common-lagom"))
  .settings(
    name := "common-lagom"
  )

  .settings(commonLagomAPISettings: _*)
  .settings(commonLagomImplSettings: _*)


lazy val `lagom-api` = (project in file("modules/example/lagom-api"))
  .settings(name := "lagom-api")
  .settings(commonLagomAPISettings: _*)

lazy val `lagom-impl` = (project in file("modules/example/lagom-impl"))
  .enablePlugins(LagomScala)
  .settings(name := "lagom-impl")
  .settings(commonLagomImplSettings: _*)
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`common-lagom`)
  .dependsOn(`lagom-api`)

lagomCassandraEnabled in ThisBuild := false
lagomUnmanagedServices in ThisBuild := Map("cas_native" -> "http://localhost:9042")
