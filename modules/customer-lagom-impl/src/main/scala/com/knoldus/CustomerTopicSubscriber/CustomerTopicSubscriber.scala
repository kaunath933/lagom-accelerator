package com.knoldus.CustomerTopicSubscriber

import akka.kafka.scaladsl.Producer
import akka.{Done, NotUsed}
import akka.stream.scaladsl.Flow
import com.knoldus.CustomerEntity
import com.knoldus.api.{CustomerDetails, CustomerKafkaApi}
import com.knoldus.command.{CreateCustomerCommand, GetCustomerCommand}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CustomerTopicServiceFlow(registry: PersistentEntityRegistry) {

  val customerDetailsFlow: Flow[CustomerDetails, Done, _] = Flow[CustomerDetails].mapAsync(8){
    details =>
      registry.refFor[CustomerEntity](details.id).ask{
      GetCustomerCommand(details.id)
    }
      Future{Done}
  }

  val customerTopicsDetailsFlow: Flow[CustomerDetails, Done, _] = Flow[CustomerDetails].via {
    customerDetailsFlow
  }

}

class CustomerTopicSubscriber(customerServiceKafka: CustomerKafkaApi, customerTopicServiceFlow: CustomerTopicServiceFlow) {

  customerServiceKafka.publishDetailsToKafka.subscribe.atLeastOnce {
    customerTopicServiceFlow.customerTopicsDetailsFlow
  }






  //Flow[Message]
    //.map(toDevolved)
    //.via(CommonFlows.splitFlow[Devolved, Done](
      //_.withinSize(maxMessageSize),
      //Flow[Devolved]
        //.map(_.message(topic))
        //.via(producerOpt match {
          case Some(producer) => Producer.flow(producerSettings, producer)
          case None => Producer.flow(producerSettings)
        })
        .map(_ => Done),
      Flow[Devolved].map(d => (deadLetterMessage(topic, d), d)).via(log.error(t => t._1))
        .map(t => oculus.akka.streams.DeadLetter(topic, t._2.key, t._2.value, t._1))
        .via(deadLetterFlow match {
          case Some(dlf) => dlf
          case _ => CommonFlows.identityFlow[DeadLetter]
        })
        .map(_ => Done)
    ))
}
