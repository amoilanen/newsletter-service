package com.github.amoilanen.repositories

import com.github.amoilanen.models.{Newsletter, NewsletterId, NewsletterIssue, NewsletterOwner, NewsletterSubscriber}
import com.github.amoilanen.models.attributes.NewsletterAttributes
import doobie.ConnectionIO
import doobie.implicits.*

class NewsletterRepository {

  def createNewsletter(newsletterAttributes: NewsletterAttributes): ConnectionIO[Newsletter] =
    val NewsletterOwner(name, email) = newsletterAttributes.owner
    val title = newsletterAttributes.title
    for
      id <- sql"insert into newsletter (title, owner_name, owner_email) values ($title, $name, $email)"
        .update
        .withUniqueGeneratedKeys[BigDecimal]("id")
    yield
      Newsletter(NewsletterId(id), newsletterAttributes, subscribers = Set.empty, issues = Seq.empty)
}
