import sbt.Keys._
import sbt._

name := "lagom-acclerator"

version in ThisBuild := "1.0.0"

scalaVersion in ThisBuild := "2.12.8"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test

lazy val `lagom-acclerator` = (project in file("."))
  .aggregate(`common-lagom`, `customer-lagom-api`, `customer-lagom-impl`)

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

lazy val `customer-lagom-api` = (project in file("modules/customer-lagom-api"))
  .settings(name := "lagom-api")
  .settings(commonLagomAPISettings: _*)

lazy val `customer-lagom-impl` = (project in file("modules/customer-lagom-impl"))
  .enablePlugins(LagomScala)
  .settings(name := "lagom-impl")
  .settings(commonLagomImplSettings: _*)
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`common-lagom`)
  .dependsOn(`customer-lagom-api`)

lagomCassandraEnabled in ThisBuild := false
lagomUnmanagedServices in ThisBuild := Map("cas_native" -> "http://localhost:9042")
