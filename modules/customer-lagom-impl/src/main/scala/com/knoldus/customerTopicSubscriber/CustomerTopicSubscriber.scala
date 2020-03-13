package com.knoldus.customerTopicSubscriber

import akka.actor.ActorSystem
import akka.stream.scaladsl.Flow
import akka.{Done, NotUsed}
import com.knoldus.api.{CustomerDetails, CustomerKafkaApi}
import com.knoldus.command.{CreateCustomerCommand, GetCustomerCommand}
import com.knoldus.{CustomerEntity, KafkaProducerFlow}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry

class CustomerTopicServiceFlow(registry: PersistentEntityRegistry, actorSystem: ActorSystem) {

  import KafkaProducerFlow._

  val customerDetailsFlow: Flow[CustomerDetails, CreateCustomerCommand#ReplyType, NotUsed] = Flow[CustomerDetails].mapAsync(8) {
    details =>
      registry.refFor[CustomerEntity](details.id).ask {
        CreateCustomerCommand(details)
      }
  }

  val customerTopicsDetailsFlow: Flow[CustomerDetails, Done, NotUsed] = Flow[CustomerDetails].via(customerDetailsFlow)
    .map(_ => producerFlow("customer")(actorSystem)).map(_ => Done)

}

class CustomerTopicSubscriber(customerServiceKafka: CustomerKafkaApi, customerTopicServiceFlow: CustomerTopicServiceFlow) {

  customerServiceKafka.publishDetailsToKafka.subscribe.atLeastOnce {
    customerTopicServiceFlow.customerTopicsDetailsFlow
  }

}
