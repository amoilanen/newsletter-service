package com.github.amoilanen.services

import cats.effect.IO
import doobie.util.transactor.Transactor
import doobie.implicits.*
import com.github.amoilanen.models.{Newsletter, NewsletterId}
import com.github.amoilanen.models.attributes.NewsletterAttributes
import com.github.amoilanen.repositories.NewsletterRepository

trait NewsletterService:
  def createNewsletter(newsletterAttributes: NewsletterAttributes): IO[NewsletterId]
  def getNewsletters(): IO[List[Newsletter]]

object NewsletterService:
  def impl(transactor: Transactor[IO], newsletterRepository: NewsletterRepository): NewsletterService =
    new NewsletterService:
      override def getNewsletters(): IO[List[Newsletter]] =
        //TODO: Implement
        IO.pure(List.empty[Newsletter])

      override def createNewsletter(newsletterAttributes: NewsletterAttributes): IO[NewsletterId] =
        newsletterRepository.createNewsletter(newsletterAttributes).transact(transactor)
