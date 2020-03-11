package com.knoldus.service

import akka.actor.FSM.Event
import akka.{Done, NotUsed}
import com.knoldus.Constants.QueryConstants
import com.knoldus.CustomerEntity
import com.knoldus.api.{CustomerApi, CustomerDetails}
import com.knoldus.command.{Commands, CreateCustomerCommand, DeleteCustomerCommand}
import com.knoldus.events.Events
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.transport.{ExceptionMessage, NotFound, TransportErrorCode}
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRef, PersistentEntityRegistry}

import scala.concurrent.ExecutionContext

/**
 *
 * @param persistentEntityRegistry  - PersistentEntityRegistry
 * @param session                   - Cassandra Session
 * @param ec                        - Execution Context
 */
class CustomerServiceImpl( persistentEntityRegistry: PersistentEntityRegistry, session: CassandraSession)(implicit ec: ExecutionContext) extends CustomerApi {

  /**
   * It will fetch the a customer details from cassandra
   *
   * @param id  - The customers id
   * @return ServiceCall[NotUsed, CustomerDetails]
   */
  override def getCustomerDetails(id: String): ServiceCall[NotUsed, CustomerDetails] = ServiceCall { _ =>
    session.selectOne(QueryConstants.GET_PRODUCT, id).map {
      case Some(row) => CustomerDetails.apply(row.getString("id"), row.getString("name"), row.getString("email"))
      case None => throw new NotFound(TransportErrorCode.NotFound, new ExceptionMessage("Customer Id Not Found",
        "Customer with this customer id does not exist"))
    }
  }

  /**
   * It will fetch all the customers details from cassandra
   * @return  ServiceCall[NotUsed, List[CustomerDetails]]
   */
  override def getAllCustomers(): ServiceCall[NotUsed, List[CustomerDetails]] = ServiceCall { _ =>
    session.selectAll(QueryConstants.GET_ALL_PRODUCTS).map {
      row => row.map(customer => CustomerDetails(customer.getString("id"), customer.getString("name"), customer.getString("email"))).toList
    }
  }

  /**
   * This will take a customer details as json string and stores it in cassandra
   *
   * @return ServiceCall[CustomerDetails, String]
   */
  override def addCustomer(): ServiceCall[CustomerDetails, String] = {
    ServiceCall { request =>
      ref(request.id).ask(CreateCustomerCommand(request)).map {
        case Done => {
          s"${request.name}, you are registered"
        }
      }
    }
  }

  /**
   * It will take an id and deletes the customer details with the respective id from database
   *
   * @param id  - The customers id
   * @return    - ServiceCall[NotUsed, Done]
   */
  override def deleteCustomer(id: String): ServiceCall[NotUsed, Done] = ServiceCall { _ =>
    ref(id).ask(DeleteCustomerCommand(id)).map(_ => {
      Done.getInstance()
    })

  }

//  override def publishDetailsToKafka : Topic[CustomerDetails] = TopicProducer.taggedStreamWithOffset(Events.Tag.allTags.toList) { (tag, offset) =>
//    persistentEntityRegistry.eventStream(tag, offset)
//      .collect{
//        case  EventStreamElement(str, event, offset) =>
//      }
//
//  }
  def ref(id: String): PersistentEntityRef[Commands[_]] = {
    persistentEntityRegistry
      .refFor[CustomerEntity](id)
  }
}
