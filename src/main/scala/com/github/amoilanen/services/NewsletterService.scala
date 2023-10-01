package com.github.amoilanen.services

import cats.effect.IO
import doobie.util.transactor.Transactor
import doobie.implicits.*

import com.github.amoilanen.models.NewsletterId
import com.github.amoilanen.models.attributes.NewsletterAttributes
import com.github.amoilanen.repositories.NewsletterRepository

trait NewsletterService:
  def createNewsletter(newsletterAttributes: NewsletterAttributes): IO[NewsletterId]

object NewsletterService:
  def impl(transactor: Transactor[IO], newsletterRepository: NewsletterRepository): NewsletterService =
    new NewsletterService:
      def createNewsletter(newsletterAttributes: NewsletterAttributes): IO[NewsletterId] =
        newsletterRepository.createNewsletter(newsletterAttributes).transact(transactor)
