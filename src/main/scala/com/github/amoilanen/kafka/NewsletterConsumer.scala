package com.github.amoilanen.kafka

import fs2.Stream
import cats.effect.IO

import cats.syntax.option.*
import fs2.concurrent.{Signal, SignallingRef}

import com.github.amoilanen.kafka.common.{KafkaConsumer, KafkaConsumerConfig, KafkaMessage}

import scala.concurrent.duration.*

//TODO: Start the consumer as a part of the overall app
//TODO: Parse JSON message
class NewsletterConsumer(config: KafkaConsumerConfig):

  def newsletterStream(stopSignal: Signal[IO, Boolean]): Stream[IO, KafkaMessage[String, String]] =
    KafkaConsumer(config).kafkaStream(stopSignal)

@main
def newsletterConsumerMain(): Unit =
  import cats.effect.unsafe.implicits.global
  val kafkaConsumerConfig = KafkaConsumerConfig(
    topic = "newsletters",
    pollRate = 250.milliseconds,
    servers = "localhost:9092",
    autoCommit = true,
    //TODO: If autoCommit is false, then commit the offset explicitly in the stream
    autoCommitInterval = 250.milliseconds.some
  )
  val consumer = NewsletterConsumer(kafkaConsumerConfig)
  val app = for
    signal <- SignallingRef[IO, Boolean](false)
    _ <- IO(scala.sys.addShutdownHook(() => signal.set(true).unsafeRunSync()))
    _ <- consumer.newsletterStream(signal).compile.foldChunks(IO.unit)((_, chunk) =>
      chunk.foreach(message =>
        println(s"consumed: offset = ${message.offset}, value = ${message.value}")
      )
      IO.unit
    )
  yield
    ()
  app.unsafeRunSync()