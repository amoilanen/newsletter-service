package com.github.amoilanen.models

import com.github.amoilanen.models.attributes.NewsletterAttributes

case class Newsletter(id: NewsletterId, attributes: NewsletterAttributes, subscribers: Set[NewsletterSubscriber] = Set.empty, issues: Seq[NewsletterIssue] = Seq.empty)
