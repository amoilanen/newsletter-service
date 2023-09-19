package com.github.amoilanen.models.attributes

import com.github.amoilanen.models.NewsletterOwner
import doobie.Meta

case class NewsletterAttributes(title: String, owner: NewsletterOwner)