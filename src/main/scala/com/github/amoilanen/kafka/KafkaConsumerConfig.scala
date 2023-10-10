package com.github.amoilanen.kafka

import java.util.Properties
import scala.concurrent.duration.FiniteDuration

case class KafkaConsumerConfig(topic: String, pollRate: FiniteDuration, servers: String,
                               autoCommit: Boolean, autoCommitInterval: Option[FiniteDuration],
                               keyDeserializer: String,
                               valueDeserializer: String):
  lazy val asProperties: Properties =
    val properties = new Properties
    properties.put("bootstrap.servers", servers)
    properties.put("group.id", topic)
    properties.put("enable.auto.commit", autoCommit.toString)
    properties.put("auto.commit.interval.ms", autoCommitInterval.map(_.toMillis).getOrElse(0).toString)
    properties.put("key.deserializer", keyDeserializer)
    properties.put("value.deserializer", valueDeserializer)
    properties