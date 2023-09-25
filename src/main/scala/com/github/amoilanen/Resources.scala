package com.github.amoilanen

import pureconfig.ConfigSource
import com.comcast.ip4s.{Host, Port, port}
import cats.effect.{IO, Resource}
import doobie.util.transactor.Transactor
import org.http4s.server.Server
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import sttp.tapir.server.http4s.Http4sServerInterpreter

import scala.util.Try

import com.github.amoilanen.conf.AppConfig
import com.github.amoilanen.db.Database

case class Resources(config: AppConfig, transactor: Transactor[IO], httpServer: Server)

object Resources:

  def loadConfig(): Resource[IO, AppConfig] =
    val source = ConfigSource.default
    Resource.eval(IO.fromTry(Try(source.loadOrThrow[AppConfig])))

  def setupEmberServer(): Resource[IO, Server] =
    val routes = Http4sServerInterpreter[IO]().toRoutes(Endpoints.all)

    //TODO: Use configuration values
    val port = sys.env
      .get("HTTP_PORT")
      .flatMap(_.toIntOption)
      .flatMap(Port.fromInt)
      .getOrElse(port"8080")

    EmberServerBuilder
      .default[IO]
      .withHost(Host.fromString("localhost").get)
      .withPort(port)
      .withHttpApp(Router("/" -> routes).orNotFound)
      .build

  def loadResources(): Resource[IO, Resources] =
    for
      config <- loadConfig()
      dataSource <- Database.createDataSource(config.database)
      transactor <- Database.createTransactor(config.database, dataSource)
      _ <- Resource.eval(Database.runMigrations(dataSource))
      httpServer <- setupEmberServer()
    yield Resources(config, transactor, httpServer)