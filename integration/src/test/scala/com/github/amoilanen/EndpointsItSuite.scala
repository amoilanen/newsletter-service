package com.github.amoilanen

import io.circe.generic.auto.*
import cats.effect.IO

import sttp.tapir.server.stub.TapirStubInterpreter
import sttp.client3.*
import sttp.client3.circe.*
import sttp.client3.{SttpBackend, UriContext}
import sttp.client3.testing.SttpBackendStub
import sttp.tapir.integ.cats.effect.CatsMonadError

import munit.CatsEffectSuite

import com.github.amoilanen.MockitoSugar
import com.github.amoilanen.models.NewsletterOwner
import com.github.amoilanen.models.attributes.NewsletterAttributes
import com.github.amoilanen.services.NewsletterService

class EndpointsItSuite extends CatsEffectSuite with MockitoSugar:

  test("Newsletter can be created via post") {
    //TODO: Mock newsletterService interactions and verify them
    val newsletterService = mock[NewsletterService]
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
    yield
      //TODO: Assert more about the response
      assert(response.body.isRight)
  }
