package com.github.amoilanen

import cats.effect.{IO, Resource}
import com.comcast.ip4s.{Host, Port, port}
import com.github.amoilanen.services.NewsletterService
import org.http4s.server.Server
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import sttp.tapir.server.http4s.Http4sServerInterpreter

object Server:

  def setup(newsletterService: NewsletterService): Resource[IO, Server] =
    val routes = Http4sServerInterpreter[IO]().toRoutes(Endpoints(newsletterService).all)

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