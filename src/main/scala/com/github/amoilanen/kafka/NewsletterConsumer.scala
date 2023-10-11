package com.github.amoilanen.kafka

import fs2.Stream
import cats.effect.{IO, Resource}
import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer}

import java.time.Duration
import cats.syntax.option.*
import fs2.concurrent.{Signal, SignallingRef}

import scala.concurrent.duration.*
import scala.jdk.CollectionConverters.*

//TODO: Start the consumer as a part of the overall app
//TODO: Parse JSON message
class NewsletterConsumer(kafkaConsumerConfig: KafkaConsumerConfig):

  def kafkaStream(stopSignal: Signal[IO, Boolean]): Stream[IO, KafkaMessage[String, String]] =
    (for
      consumer <- Stream.resource(createConsumer(kafkaConsumerConfig))
      message <- Stream
        .awakeEvery[IO](kafkaConsumerConfig.pollRate)
        .evalMap(_ =>
          IO(readMessages(consumer))
        ).flatMap(Stream.emits(_))
    yield
      message).interruptWhen(stopSignal)

  private def createConsumer(kafkaConsumerConfig: KafkaConsumerConfig): Resource[IO, KafkaConsumer[String, String]] =
    Resource.make(
      IO {
        val consumer = new KafkaConsumer[String, String](kafkaConsumerConfig.asProperties)
        consumer.subscribe(List(kafkaConsumerConfig.topic).asJava)
        consumer
      }
    )(consumer => IO(consumer.close()))

  private def readMessages(consumer: KafkaConsumer[String, String]): List[KafkaMessage[String, String]] =
    val response: ConsumerRecords[String, String] = consumer.poll(Duration.ofMillis(kafkaConsumerConfig.pollRate.toMillis))
    val records = response.records(kafkaConsumerConfig.topic).iterator().asScala.toList
    records.map(KafkaMessage.from(_))

@main
def newsletterConsumerMain(): Unit =
  import cats.effect.unsafe.implicits.global
  val kafkaConsumerConfig = KafkaConsumerConfig(
    topic = "newsletters",
    pollRate = 250.milliseconds,
    servers = "localhost:9092",
    autoCommit = true,
    autoCommitInterval = 250.milliseconds.some
  )
  val consumer = NewsletterConsumer(kafkaConsumerConfig)
  val app = for
    signal <- SignallingRef[IO, Boolean](false)
    _ <- IO(scala.sys.addShutdownHook(() => signal.set(true).unsafeRunSync()))
    _ <- consumer.kafkaStream(signal).compile.foldChunks(IO.unit)((_, chunk) =>
      chunk.foreach(message =>
        println(s"consumed: offset = ${message.offset}, value = ${message.value}")
      )
      IO.unit
    )
  yield
    ()
  app.unsafeRunSync()