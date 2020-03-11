package com.knoldus.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceAcl, ServiceCall}

/**
 * The customer service trait
 */
trait CustomerApi extends Service {

  def getAllCustomers: ServiceCall[NotUsed, List[CustomerDetails]]

  def getCustomerDetails(id: String): ServiceCall[NotUsed, CustomerDetails]

  def addCustomer(): ServiceCall[CustomerDetails, String]

  def deleteCustomer(id: String): ServiceCall[NotUsed, Done]

  //def publishDetailsToKafka: Topic[CustomerDetails]

  override final def descriptor: Descriptor = {
    import Service._

    named("lagom-impl")
      .withCalls(
        restCall(Method.GET,"/api/details/get",getAllCustomers),
        restCall(Method.GET, "/api/details/get/:id", getCustomerDetails _),
        restCall(Method.POST, "/api/details/add/", addCustomer _ ),
        restCall(Method.DELETE, "/api/delete/:id", deleteCustomer _)
     // )
      //.withTopics(
      //topic("customer", publishDetailsToKafka).addProperty(KafkaProperties.partitionKeyStrategy, PartitionKeyStrategy[CustomerDetails](_.id))
    ).withAutoAcl(true)

  }
}
