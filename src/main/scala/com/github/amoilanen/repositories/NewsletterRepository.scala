package com.github.amoilanen.repositories

import com.github.amoilanen.models.{
  Newsletter,
  NewsletterId,
  NewsletterIssue,
  NewsletterOwner,
  NewsletterSubscriber
}
import com.github.amoilanen.models.attributes.NewsletterAttributes
import doobie.ConnectionIO
import doobie.implicits.*

trait NewsletterRepository:
  def createNewsletter(newsletterAttributes: NewsletterAttributes): ConnectionIO[NewsletterId]

object NewsletterRepository:
  def impl(): NewsletterRepository =
    new NewsletterRepository:
      override def createNewsletter(newsletterAttributes: NewsletterAttributes): ConnectionIO[NewsletterId] =
        val NewsletterOwner(name, email) = newsletterAttributes.owner
        val title = newsletterAttributes.title
        for id <-
              sql"insert into newsletter (title, owner_name, owner_email) values ($title, $name, $email)".update
                .withUniqueGeneratedKeys[BigDecimal]("id")
        yield NewsletterId(id)
