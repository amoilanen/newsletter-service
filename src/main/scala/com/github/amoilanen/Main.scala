package com.github.amoilanen

import cats.effect.IOApp
import cats.effect.IO
import cats.effect.Resource
import com.github.amoilanen.conf.AppConfig
import com.github.amoilanen.db.Database
import com.github.amoilanen.repositories.NewsletterRepository
import com.github.amoilanen.services.NewsletterService
import doobie.*
import doobie.implicits.*
import doobie.hikari.*
import doobie.util.transactor.Transactor
import org.http4s.ember.server.EmberServerBuilder
import pureconfig.ConfigSource

import scala.util.Try

object Main extends IOApp.Simple:

  override val run: IO[Unit] =
    Resources.loadResources().use({ case Resources(config, transactor) =>
      for
        _ <- IO(println(config.database.url))
        result <- sql"select 42".query[Int].unique.transact(transactor)
        _ <- IO(println(result))
        _ <- Server.setup(NewsletterService.impl(transactor, NewsletterRepository.impl())).use(httpServer =>
          for
            _ <- IO.println(s"Go to http://localhost:${httpServer.address.getPort}/docs to open SwaggerUI. Press ENTER key to exit.")
            _ <- IO.readLine
          yield
            ()
        )
      yield ()
    })
