package com.github.amoilanen.db

import cats.syntax.either.*
import cats.syntax.option.*
import cats.effect.{IO, Resource}
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import doobie.{ExecutionContexts, Transactor}
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.output.{MigrateErrorResult, MigrateResult}

import javax.sql.DataSource
import scala.jdk.CollectionConverters.*
import javax.sql.DataSource
import scala.collection.mutable.ArrayBuffer
import scala.util.Try
import com.github.amoilanen.conf.DatabaseConfig
import com.github.amoilanen.error.ApplicationError

object Database:

  def createDataSource(databaseConfig: DatabaseConfig): Resource[IO, DataSource] =
    Resource.pure({
      val config = HikariConfig()
      config.setDriverClassName(databaseConfig.driver)
      config.setJdbcUrl(databaseConfig.url)
      config.setUsername(databaseConfig.user)
      config.setPassword(databaseConfig.password)
      config.setMaximumPoolSize(databaseConfig.maxPoolSize)
      HikariDataSource(config)
    })

  def createTransactor(
      databaseConfig: DatabaseConfig,
      dataSource: DataSource
  ): Resource[IO, Transactor[IO]] =
    for ce <- ExecutionContexts.fixedThreadPool[IO](databaseConfig.maxPoolSize)
    yield Transactor.fromDataSource[IO](dataSource, ce)

  def runMigrations(dataSource: DataSource): IO[Unit] =
    IO.fromEither(
      for
        flyway          <- Try(Flyway.configure.dataSource(dataSource).load).toEither
        migrationResult <- Try(flyway.migrate()).toEither
        result <- migrationResult match
          case result: MigrateErrorResult =>
            val error = result.error
            ApplicationError(
              s"Migration failed, ${result}, message = ${error.message}, errorCode = ${error.errorCode}, stacktrace = ${error.stackTrace}"
            ).asLeft
          case _ =>
            ().asRight
      yield result
    )
