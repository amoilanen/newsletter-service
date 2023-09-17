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
  }

  val catsEffect = Seq(
    // "core" module - IO, IOApp, schedulers
    // This pulls in the kernel and std modules automatically.
    "org.typelevel" %% "cats-effect",
    // concurrency abstractions and primitives (Concurrent, Sync, Async etc.)
    "org.typelevel" %% "cats-effect-kernel",
    // standard "effect" library (Queues, Console, Random etc.)
    "org.typelevel" %% "cats-effect-std"
  ).map(_ % Version.CatsEffect)
  val catsCore = "org.typelevel" %% "cats-core" % Version.CatsCore
  val munitCatsEffect = "org.typelevel" %% "munit-cats-effect-3" % Version.MunitCatsEffect
  val flywayDb = "org.flywaydb" % "flyway-core" % Version.FlywayDb
  val pureconfigCore = "com.github.pureconfig" %% "pureconfig-core" % Version.PureconfigCore
  val postgresqlDriver = "org.postgresql" % "postgresql" % Version.PostgresqlDriver
  val doobie = Seq(
    "org.tpolecat" %% "doobie-core",
    "org.tpolecat" %% "doobie-hikari",
    "org.tpolecat" %% "doobie-postgres"
  ).map(_ % Version.Doobie)
   // Postgres driver 42.6.0 + type mappings.
  val hikari = "com.zaxxer" % "HikariCP" % Version.Hikari

  val dependencies = catsEffect ++ Seq(catsCore) ++ Seq(flywayDb) ++ Seq(pureconfigCore) ++ Seq(postgresqlDriver) ++ doobie ++ Seq(hikari)

  val testDependencies = Seq(munitCatsEffect).map(_ % Test)

  val allDependencies = dependencies ++ testDependencies
}
