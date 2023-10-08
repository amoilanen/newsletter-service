package com.github.amoilanen.kafka

import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer}
import java.time.Duration
import java.util
import java.util.Properties
import scala.util.Using

//TODO: Start the consumer as a part of the overall app
//TODO: Use fs2
//TODO: Parse JSON message
class NewsletterConsumer:

  //TODO: This should be configurable
  val servers = "localhost:9092"

  //TODO: This should be configurable
  val topic = "newsletters"

  //TODO: This should be configurable
  val pollDurationMilliseconds = 250

  private var keepConsuming: Boolean = true

  private val consumerProperties =
    val properties = new Properties
    properties.put("bootstrap.servers", servers)
    properties.put("group.id", topic)
    properties.put("enable.auto.commit", "true")
    properties.put("auto.commit.interval.ms", "1000")
    properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    properties

  def consume(): Unit =
    Using(new KafkaConsumer[String, String](consumerProperties)) { consumer =>
      consumer.subscribe(util.List.of(topic))
      while (keepConsuming)
        val records: ConsumerRecords[String, String] = consumer.poll(Duration.ofMillis(pollDurationMilliseconds)) //<3>
        records.forEach(record =>
          println(s"consumed: offset = ${record.offset}, value = ${record.value}")
        )
    }

  def shutdown(): Unit =
    keepConsuming = false

@main
def newsletterConsumerMain(): Unit =
  val consumer = NewsletterConsumer()
  scala.sys.addShutdownHook(() => consumer.shutdown())
  consumer.consume()
