package com.github.amoilanen.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord

case class KafkaMessage[K, V](key: K, value: V, offset: Long)

object KafkaMessage:
  def from[K, V](record: ConsumerRecord[K, V]) =
    KafkaMessage(record.key, record.value, record.offset)

