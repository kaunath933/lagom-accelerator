package com.knoldus

import akka.Done
import com.knoldus.api.{CustomerApi, CustomerDetails}
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class CustomerServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  lazy val server = ServiceTest.startServer(
    ServiceTest.defaultSetup.withCassandra(true)) {
    ctx => new CustomerServiceApplication(ctx) with LocalServiceLocator
  }

  lazy val client = server.serviceClient.implement[CustomerApi]

  override def beforeAll() = {

    server
    val session: CassandraSession = server.application.cassandraSession

    createSchema(session)

    //Add some fake data for testing purpose.
    populateData(session)

  }

  private def createSchema(session: CassandraSession): Unit = {

    //Create Keyspace
    val createKeyspace = session.executeWrite("CREATE KEYSPACE customer WITH replication = {'class': 'SimpleStrategy','replication_factor': 1};")
    Await.result(createKeyspace, 10.seconds)


    //Create table
    val createTable = session.executeCreateTable(
      """CREATE TABLE customer (
        |id text PRIMARY KEY, name text, email text)""".stripMargin)
    Await.result(createTable, 10.seconds)
  }

  private def populateData(session: CassandraSession): Unit = {
    val customer = CustomerDetails("1", "john", "jo@gmail.com")
    val insertProduct: Future[Done] = session.executeWrite("INSERT INTO customer (id, name, email) VALUES (?, ?, ?)", customer.id, customer.name, customer.email)
    Await.result(insertProduct, 10.seconds)
  }

  "Customer service" should {
    val customer = CustomerDetails("1", "john", "jo@gmail.com")
    "should return customerData by id" in {
      client.getCustomerDetails("1").invoke().map { response =>
        println(response + "response")
        response should ===(customer)

      }
    }
  }

  "Customer service" should {
    val customer = CustomerDetails("1", "john", "jo@gmail.com")
    "should return list of customerData" in {
      client.getAllCustomers.invoke().map { response =>
        println(response + "response")
        response should ===(List(customer))

      }
    }
  }


  "Customer service" should {
    val customer = CustomerDetails("1", "john", "jo@gmail.com")
    "should add a new customer using customer object" in {
      client.addCustomer().invoke(customer).map { response =>
        println(response + "response")
        response should ===("john, you are registered")

      }
    }
  }

  "Customer service" should {
    val customer = CustomerDetails("1", "john", "jo@gmail.com")
    "should delete customerData by id" in {
      client.deleteCustomer("1").invoke().map { response =>
        println(response + "response")
        response should ===(Done.getInstance())

      }
    }
  }

  override protected def afterAll() = server.stop()
}
