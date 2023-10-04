package com.github.amoilanen

import io.circe.generic.auto.*
import io.circe.parser.decode
import cats.syntax.either._
import cats.effect.IO

import sttp.client3.*
import sttp.client3.circe.*
import sttp.client3.{SttpBackend, UriContext}
import sttp.tapir.server.stub.TapirStubInterpreter
import sttp.client3.testing.SttpBackendStub
import sttp.tapir.integ.cats.effect.CatsMonadError

import munit.CatsEffectSuite
import org.mockito.Mockito.{ when, verify, times }
import org.mockito.ArgumentMatchers.any

import com.github.amoilanen.models.{NewsletterId, NewsletterOwner}
import com.github.amoilanen.models.attributes.NewsletterAttributes
import com.github.amoilanen.services.NewsletterService

class EndpointsItSuite extends CatsEffectSuite with MockitoSugar:

  test("POST /newsletters returns new NewsletterId when it can be created") {
    val newsletterId = NewsletterId(BigDecimal(1))
    val newsletterService = mock[NewsletterService]
    when(newsletterService.createNewsletter(any[NewsletterAttributes])).thenReturn(IO.pure(newsletterId))
    val endpoints = Endpoints(newsletterService)

    val backendStub: SttpBackend[IO, Any] = TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
      .whenServerEndpoint(endpoints.postNewsletterServerEndpoint)
      .thenRunLogic()
      .backend()

    val newsletterAttributes = NewsletterAttributes(
      title = "Some newsletter",
      owner = NewsletterOwner(name = "Some owner", email = "some@example.com")
    )

    for
      response <- basicRequest
        .post(uri"http://localhost/newsletters")
        .body(newsletterAttributes)
        .send(backendStub)
      body: String <- IO.fromEither(response.body.leftMap(RuntimeException(_)))
      returnedId <- IO.fromEither(decode[NewsletterId](body))
    yield
      assertEquals(returnedId, newsletterId)
      verify(newsletterService, times(1)).createNewsletter(newsletterAttributes)
  }
