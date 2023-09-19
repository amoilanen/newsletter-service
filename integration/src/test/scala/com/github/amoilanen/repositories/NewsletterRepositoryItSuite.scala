package com.github.amoilanen.repositories

import cats.effect.IO
import com.github.amoilanen.Resources
import com.github.amoilanen.models.Newsletter
import com.github.amoilanen.models.NewsletterOwner
import com.github.amoilanen.models.attributes.NewsletterAttributes
import munit.CatsEffectSuite
import doobie.ConnectionIO
import doobie.implicits.*

class NewsletterRepositoryItSuite extends CatsEffectSuite {

  test("Newsletter can be persisted") {
    Resources.loadResources().use(resources =>
      val transactor = resources.transactor
      val repository = NewsletterRepository.impl()
      val newsletterAttributes = NewsletterAttributes(title = "Newsletter1", owner = NewsletterOwner("Owner1", "owner1@mail.com"))
      val test = for
        _ <- sql"truncate table newsletter cascade".update.run
        persistedId <- repository.createNewsletter(newsletterAttributes)
        persistedNewsletters <- sql"select * from newsletter".query[Newsletter].to[List]
        expectedPersistedNewsletter = Newsletter(persistedId, newsletterAttributes)
      yield
        assertEquals(persistedNewsletters, List(expectedPersistedNewsletter))
      test.transact(transactor)
    )
  }
}
