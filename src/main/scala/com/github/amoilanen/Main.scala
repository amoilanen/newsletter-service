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

  override val run: IO[Unit] =
    Resources.loadResources().use({ case Resources(config, transactor) =>
      for
        _      <- IO(println(config.database.url))
        result <- sql"select 42".query[Int].unique.transact(transactor)
        _      <- IO(println(result))
      yield ()
    })
