//package com.knoldus.api
//
//import com.lightbend.lagom.scaladsl.api.{Descriptor, Service}
//import com.lightbend.lagom.scaladsl.api.broker.Topic
//
//trait CustomerServiceKafka extends Service {
//
//  def publishDetailsToKafka: Topic[CustomerDetails]
//
//  final override def descriptor: Descriptor = {
//    import Service._
//
//    named("CustomerDetailsKafka").withTopics(
//      topic("customer", publishDetailsToKafka)
//    )
//  }
//}
