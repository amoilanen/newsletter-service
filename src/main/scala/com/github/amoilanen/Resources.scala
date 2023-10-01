package com.github.amoilanen

import pureconfig.ConfigSource
import cats.effect.{IO, Resource}
import doobie.util.transactor.Transactor

import scala.util.Try

import com.github.amoilanen.conf.AppConfig
import com.github.amoilanen.db.Database

case class Resources(config: AppConfig, transactor: Transactor[IO])

object Resources:

  def loadConfig(): Resource[IO, AppConfig] =
    val source = ConfigSource.default
    Resource.eval(IO.fromTry(Try(source.loadOrThrow[AppConfig])))

  def loadResources(): Resource[IO, Resources] =
    for
      config <- loadConfig()
      dataSource <- Database.createDataSource(config.database)
      transactor <- Database.createTransactor(config.database, dataSource)
      _ <- Resource.eval(Database.runMigrations(dataSource))
    yield Resources(config, transactor)