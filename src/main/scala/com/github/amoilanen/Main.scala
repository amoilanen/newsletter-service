package com.github.amoilanen

import cats.effect.IOApp
import cats.effect.IO
import com.github.amoilanen.conf.AppConfig
import pureconfig.ConfigSource

import scala.util.Try

object Main extends IOApp.Simple:

  override val run: IO[Unit] =
    val source = ConfigSource.default
    for
      config: AppConfig <- IO.fromTry(Try(source.loadOrThrow[AppConfig]))
      _ <- IO(println(config.database.url))
    yield
      ()
