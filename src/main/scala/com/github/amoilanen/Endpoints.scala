package com.github.amoilanen

import sttp.tapir.*
import cats.effect.IO
import cats.syntax.either.*
import io.circe.generic.auto.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import com.github.amoilanen.models.{Newsletter, NewsletterId, NewsletterOwner}
import com.github.amoilanen.models.attributes.NewsletterAttributes
import com.github.amoilanen.models.response.ErrorResponse
import io.circe.Json

object Endpoints:

  /*
   * POST /newsletters
   * GET /newsletters
   * GET /newsletters/:id
   * PATCH /newsletters/:id
   *
   * POST /newsletters/:id/issues
   * GET /newsletters/:id/issues
   * GET /newsletters/:id/issues/:issue_id
   * PATCH /newsletters/:id/issues/:issue_id
   */
  val getNewsletters: PublicEndpoint[Unit, ErrorResponse, List[Newsletter], Any] =
    endpoint.get
      .in("newsletters")
      .out(jsonBody[List[Newsletter]])
      .errorOut(jsonBody[ErrorResponse])
      .description("List all newsletters")
  val getNewslettersServerEndpoint = getNewsletters.serverLogic[IO](_ =>
    IO.pure(hardcodedNewsletters.asRight)
  )

  val getNewsletter: PublicEndpoint[BigDecimal, ErrorResponse, Newsletter, Any] =
    endpoint.get
      .in("newsletters" / path[BigDecimal]("newsletterId"))
      .out(jsonBody[Newsletter])
      .errorOut(jsonBody[ErrorResponse])
      .description("Get a newsletter")

  val postNewsletter: PublicEndpoint[NewsletterAttributes, ErrorResponse, Newsletter, Any] =
    endpoint.post
      .in("newsletters")
      .in(jsonBody[NewsletterAttributes])
      .out(jsonBody[Newsletter])
      .errorOut(jsonBody[ErrorResponse])
      .description("Create a new newsletter")

  //TODO: Implement the rest of the endpoints
  val apiEndpoints: List[ServerEndpoint[Any, IO]] = List(getNewslettersServerEndpoint)

  val docEndpoints: List[ServerEndpoint[Any, IO]] = SwaggerInterpreter()
    .fromServerEndpoints[IO](apiEndpoints, "newsletter-service", "0.0.1")

  val all: List[ServerEndpoint[Any, IO]] = apiEndpoints ++ docEndpoints

  private val hardcodedNewsletters = List(
    Newsletter(
      NewsletterId(BigDecimal(1)),
      NewsletterAttributes("Awesome Scala 3", NewsletterOwner("Mikko Meikäläinen", "m.meikäläinen@elisa.fi"))
    ),
    Newsletter(
      NewsletterId(BigDecimal(2)),
      NewsletterAttributes("Amazing COBOL", NewsletterOwner("Mikko Meikäläinen", "m.meikäläinen@elisa.fi"))
    ),
    Newsletter(
      NewsletterId(BigDecimal(3)),
      NewsletterAttributes("Turbo Pascal for Beginners", NewsletterOwner("Mikko Meikäläinen", "m.meikäläinen@elisa.fi"))
    )
  )