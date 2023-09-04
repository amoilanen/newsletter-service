package com.github.amoilanen

import cats.effect.IOApp
import cats.effect.IO
import cats.effect.Resource
import com.github.amoilanen.conf.AppConfig
import com.github.amoilanen.db.Database
import doobie._
import doobie.implicits._
import doobie.hikari._
import doobie.util.transactor.Transactor
import pureconfig.ConfigSource

import scala.util.Try

object Main extends IOApp.Simple:

  case class Resources(config: AppConfig, transactor: Transactor[IO])

  def loadConfig(): Resource[IO, AppConfig] =
    val source = ConfigSource.default
    Resource.eval(IO.fromTry(Try(source.loadOrThrow[AppConfig])))

  def loadResources(): Resource[IO, Resources] =
    for
      config <- loadConfig()
      dataSource <- Database.createDataSource(config.database)
      transactor <- Database.createTransactor(config.database, dataSource)
      _ <- Resource.eval(Database.runMigrations(dataSource))
    yield
      Resources(config, transactor)

  override val run: IO[Unit] =
    loadResources().use({ case Resources(config, transactor) =>
      for
        _ <- IO(println(config.database.url))
        result <- sql"select 42".query[Int].unique.transact(transactor)
        _ <- IO(println(result))
      yield
        ()
    })