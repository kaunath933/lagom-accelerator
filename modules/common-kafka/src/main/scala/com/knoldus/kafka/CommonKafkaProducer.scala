package com.knoldus.kafka

import java.util.Properties

import org.apache.kafka.clients.producer.KafkaProducer

trait CommonKafkaProducerTrait[K, V]  {
  val topic:String
  val props:Properties
  def producer:KafkaProducer[K, V]
  def close() = producer.close()
}

object CommonKafkaStringProducer extends CommonKafkaProducerTrait[String,String]{
  override val topic = "test"
  override val props = new Properties()
  props.put("bootstrap.servers","localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  override val producer = new org.apache.kafka.clients.producer.KafkaProducer[String, String](props)

}

object CommonKafkaByteProducer extends CommonKafkaProducerTrait[String,Array[Byte]]{
  override val topic: String = "testByte"
  override val props: Properties = new Properties()
  props.put("bootstrap.servers","localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")
  override val producer = new org.apache.kafka.clients.producer.KafkaProducer[String, Array[Byte]](props)
}
