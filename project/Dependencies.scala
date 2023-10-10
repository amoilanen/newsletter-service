import sbt._

object Dependencies {

  object Version {
    val CatsEffect = "3.3.12"
    val CatsCore = "2.10.0"
    val MunitCatsEffect = "1.0.7"
    val FlywayDb = "9.21.1"
    val PureconfigCore = "0.17.4"
    val PostgresqlDriver = "42.6.0"
    val Doobie = "1.0.0-RC4"
    val Hikari = "5.0.1"
    val Tapir = "1.7.6"
    val Http4s = "0.23.23"
    val Mockito = "5.5.0"
    val Sttp = "3.9.0"
    val Circe = "0.14.6"
    val KafkaClients = "3.6.0"
    val fs2 = "3.9.2"
  }

  val catsEffect = Seq(
    // "core" module - IO, IOApp, schedulers
    // This pulls in the kernel and std modules automatically.
    "cats-effect",
    // concurrency abstractions and primitives (Concurrent, Sync, Async etc.)
    "cats-effect-kernel",
    // standard "effect" library (Queues, Console, Random etc.)
    "cats-effect-std"
  ).map("org.typelevel" %% _ % Version.CatsEffect)
  val catsCore = "org.typelevel" %% "cats-core" % Version.CatsCore
  val flywayDb = "org.flywaydb" % "flyway-core" % Version.FlywayDb
  val pureconfigCore = "com.github.pureconfig" %% "pureconfig-core" % Version.PureconfigCore
  val postgresqlDriver = "org.postgresql" % "postgresql" % Version.PostgresqlDriver
  val doobie = Seq(
    "doobie-core",
    "doobie-hikari",
    "doobie-postgres"
  ).map("org.tpolecat" %% _ % Version.Doobie)
   // Postgres driver 42.6.0 + type mappings.
  val hikari = "com.zaxxer" % "HikariCP" % Version.Hikari
  val circe = "io.circe" %% "circe-core" % Version.Circe

  val http4s =
    "org.http4s" %% "http4s-ember-server" % Version.Http4s
  val tapir = Seq(
    "tapir-core",
    "tapir-http4s-server",
    "tapir-swagger-ui-bundle",
    "tapir-json-circe"
  ).map("com.softwaremill.sttp.tapir" %% _ % Version.Tapir)
  val kafkaClients =
    "org.apache.kafka" % "kafka-clients" % Version.KafkaClients
  val fs2 = "co.fs2" %% "fs2-core" % Version.fs2

  val dependencies = catsEffect ++ Seq(catsCore) ++ Seq(flywayDb) ++
    Seq(pureconfigCore) ++ Seq(postgresqlDriver) ++ doobie ++
    Seq(hikari) ++ tapir ++ Seq(http4s) ++ Seq(circe) ++ Seq(kafkaClients) ++ Seq(fs2)

  val munitCatsEffect = "org.typelevel" %% "munit-cats-effect-3" % Version.MunitCatsEffect
  val mockito = "org.mockito" % "mockito-core" % Version.Mockito
  val tapirSttpStubServer = "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server" % Version.Tapir
  val sttpClient = Seq(
    "core",
    "circe"
  ).map("com.softwaremill.sttp.client3" %% _ % Version.Sttp)

  val testDependencies = (Seq(munitCatsEffect, mockito, tapirSttpStubServer) ++ sttpClient).map(_ % Test)

  val allDependencies = dependencies ++ testDependencies
}
