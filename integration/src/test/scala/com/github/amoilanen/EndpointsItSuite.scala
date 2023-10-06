package com.github.amoilanen

import io.circe.generic.auto.*
import io.circe.parser.decode
import cats.syntax.either.*
import cats.effect.IO
import sttp.client3.*
import sttp.client3.circe.*
import sttp.client3.{SttpBackend, UriContext}
import sttp.tapir.server.stub.TapirStubInterpreter
import sttp.client3.testing.SttpBackendStub
import sttp.tapir.integ.cats.effect.CatsMonadError
import munit.CatsEffectSuite
import org.mockito.Mockito.{times, verify, when}
import org.mockito.ArgumentMatchers.any
import com.github.amoilanen.models.{Newsletter, NewsletterId, NewsletterOwner}
import com.github.amoilanen.models.attributes.NewsletterAttributes
import com.github.amoilanen.services.NewsletterService

class EndpointsItSuite extends CatsEffectSuite with MockitoSugar:

  import EndpointsItSuite._

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
        .response(asJson[NewsletterId])
        .send(backendStub)
      returnedId <- IO.fromEither(response.body)
    yield
      assertEquals(returnedId, newsletterId)
      verify(newsletterService, times(1)).createNewsletter(newsletterAttributes)
  }

  test("GET /newsletters returns the list of newsletters from the newsletterService") {
    val newsletterService = mock[NewsletterService]
    when(newsletterService.getNewsletters()).thenReturn(IO.pure(hardcodedNewsletters))
    val endpoints = Endpoints(newsletterService)

    val backendStub: SttpBackend[IO, Any] = TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
      .whenServerEndpoint(endpoints.getNewslettersServerEndpoint)
      .thenRunLogic()
      .backend()

    for
      response <- basicRequest
        .get(uri"http://localhost/newsletters")
        .response(asJson[List[Newsletter]])
        .send(backendStub)
      newsletters <- IO.fromEither(response.body)
    yield
      assertEquals(newsletters, hardcodedNewsletters)
      verify(newsletterService, times(1)).getNewsletters()
  }

object EndpointsItSuite:
  val hardcodedNewsletters = List(
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