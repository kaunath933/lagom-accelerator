package com.knoldus

import com.knoldus.api.{CustomerApi, CustomerKafkaApi}
import com.knoldus.customerTopicSubscriber.{CustomerTopicServiceFlow, CustomerTopicSubscriber}
import com.knoldus.events.CustomerEventReadSideProcessor
import com.knoldus.service.CustomerServiceImpl
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import play.api.libs.ws.ahc.AhcWSComponents

/**
 * The class CustomerServiceLoader which extends LagomApplicationLoader.
 * The LagomApplicationLoader is a play application loader for lagom
 */
class CustomerServiceLoader extends LagomApplicationLoader {

  /**
   * This method loads a lagom application in production
   *
   * @param context - The LagomApplicationContext
   * @return
   */
  override def load(context: LagomApplicationContext): LagomApplication =
    new CustomerServiceApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  /**
   * This method loads a lagom application in development environment
   *
   * @param context - The LagomApplicationContext
   * @return
   */
  override def loadDevMode(context: LagomApplicationContext): LagomApplication = {
    new CustomerServiceApplication(context) with LagomDevModeComponents
  }

  override def describeService = Some(readDescriptor[CustomerApi])
}

abstract class CustomerServiceApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[CustomerApi](wire[CustomerServiceImpl])

  //Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = CustomerSerializerRegistry

  lazy val customerTopicServiceFlow = wire[CustomerTopicServiceFlow]

  lazy val customerKafkaApi = serviceClient.implement[CustomerKafkaApi]

  cluster.registerOnMemberUp(wire[CustomerTopicSubscriber])

  // Register the lagom-persistent-entity-demo persistent entity
  persistentEntityRegistry.register(wire[CustomerEntity])
  readSide.register(wire[CustomerEventReadSideProcessor])

}
