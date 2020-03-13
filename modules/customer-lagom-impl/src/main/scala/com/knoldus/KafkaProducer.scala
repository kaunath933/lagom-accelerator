package com.knoldus

import java.util.Properties

import org.apache.kafka.clients.producer._

object ProducerExample extends App {

  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")

  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)

  val TOPIC = "customer"

  for (i <- 1 to 5) {
    val record = new ProducerRecord(TOPIC, "key", """{"id":"006","name":"ac","email":"co@gmail.com"}""")
    producer.send(record)
  }

  producer.close()
}

