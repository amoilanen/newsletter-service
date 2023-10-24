package com.github.amoilanen.kafka.common

import fs2.Stream
import cats.effect.{IO, Resource}
import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer => ApacheKafkaConsumer}

import java.time.Duration
import fs2.concurrent.Signal

import scala.jdk.CollectionConverters.*

class KafkaConsumer(config: KafkaConsumerConfig):
  def kafkaStream(stopSignal: Signal[IO, Boolean]): Stream[IO, KafkaMessage[String, String]] =
    (for
      consumer <- Stream.resource(createConsumer(config))
      message <- Stream
        .awakeEvery[IO](config.pollRate)
        .evalMap(_ =>
          IO(readMessages(consumer))
        ).flatMap(Stream.emits(_))
    yield
      message).interruptWhen(stopSignal)

  private def createConsumer(kafkaConsumerConfig: KafkaConsumerConfig): Resource[IO, ApacheKafkaConsumer[String, String]] =
    Resource.make(
      IO {
        val consumer = new ApacheKafkaConsumer[String, String](kafkaConsumerConfig.asProperties)
        consumer.subscribe(List(kafkaConsumerConfig.topic).asJava)
        consumer
      }
    )(consumer => IO(consumer.close()))

  private def readMessages(consumer: ApacheKafkaConsumer[String, String]): List[KafkaMessage[String, String]] =
    val response: ConsumerRecords[String, String] = consumer.poll(Duration.ofMillis(config.pollRate.toMillis))
    val records = response.records(config.topic).iterator().asScala.toList
    records.map(KafkaMessage.from(_))
