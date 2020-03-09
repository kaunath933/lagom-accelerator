//package com.knoldus.CustomerTopicSubscriber
//
//import akka.{Done, NotUsed}
//import akka.stream.scaladsl.Flow
//import com.knoldus.CustomerEntity
//import com.knoldus.api.{CustomerDetails, CustomerServiceKafka}
//import com.knoldus.command.{CreateCustomerCommand, GetCustomerCommand}
//import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
//
//class CustomerTopicServiceFlow(registry: PersistentEntityRegistry) {
//
//  val customerDetailsFlow = Flow[CustomerDetails].mapAsync(8){
//    details => registry.refFor[CustomerEntity](details.id).ask{
//      CreateCustomerCommand(details)
//    }
//  }
//
//  val customerTopicsDetailsFlow = Flow[CustomerDetails].via {
//    customerDetailsFlow
//  }
//
//}
//
//class CustomerTopicSubscriber(customerServiceKafka: CustomerServiceKafka,customerTopicServiceFlow: CustomerTopicServiceFlow) {
//
//  customerServiceKafka.publishDetailsToKafka.subscribe.atLeastOnce(
//    customerTopicServiceFlow.customerTopicsDetailsFlow
//
//)
//}
