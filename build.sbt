ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "3.3.0"

lazy val root = (project in file(".")).settings(
  name := "newsletter-service",
  libraryDependencies ++= Seq(
    // "core" module - IO, IOApp, schedulers
    // This pulls in the kernel and std modules automatically.
    "org.typelevel" %% "cats-effect" % "3.3.12",
    // concurrency abstractions and primitives (Concurrent, Sync, Async etc.)
    "org.typelevel" %% "cats-effect-kernel" % "3.3.12",
    // standard "effect" library (Queues, Console, Random etc.)
    "org.typelevel" %% "cats-effect-std" % "3.3.12",
    "org.typelevel" %% "munit-cats-effect-3" % "1.0.7" % Test,
    "org.flywaydb" % "flyway-core" % "9.21.1",
    "com.github.pureconfig" %% "pureconfig-core" % "0.17.4",
    "org.postgresql" % "postgresql" % "42.6.0",
    // Start with this one
    "org.tpolecat" %% "doobie-core" % "1.0.0-RC4",
    // And add any of these as needed
    "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC4", // HikariCP transactor.
    "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC4", // Postgres driver 42.6.0 + type mappings.
    "org.typelevel" %% "cats-core" % "2.10.0",
    "com.zaxxer" % "HikariCP" % "5.0.1",
  )
)
