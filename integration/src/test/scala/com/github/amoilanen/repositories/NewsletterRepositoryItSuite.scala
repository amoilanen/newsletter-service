package com.github.amoilanen.repositories

import cats.effect.{IO, Resource}
import com.github.amoilanen.Resources
import com.github.amoilanen.models.Newsletter
import com.github.amoilanen.models.NewsletterOwner
import com.github.amoilanen.models.attributes.NewsletterAttributes
import utils.DoobieSuite
import doobie.{ConnectionIO, Transactor}
import doobie.implicits.*
import doobie.util.transactor
import munit.CatsEffectSuite

class NewsletterRepositoryItSuite extends CatsEffectSuite with DoobieSuite {

  override def setupTransactor(): Resource[IO, Transactor[IO]] =
    Resources.loadResources().map(_.transactor)

  test("Newsletter can be persisted") {
    val repository = NewsletterRepository.impl()
    val newsletterAttributes = NewsletterAttributes(title = "Newsletter1", owner = NewsletterOwner("Owner1", "owner1@mail.com"))
    for
      _ <- sql"truncate table newsletter cascade".update.run
      persistedId <- repository.createNewsletter(newsletterAttributes)
      persistedNewsletters <- sql"select * from newsletter".query[Newsletter].to[List]
      expectedPersistedNewsletter = Newsletter(persistedId, newsletterAttributes)
    yield
      assertEquals(persistedNewsletters, List(expectedPersistedNewsletter))
  }
}
