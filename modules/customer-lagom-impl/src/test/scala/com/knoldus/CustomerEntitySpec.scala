package com.knoldus

import akka.actor.ActorSystem
import com.knoldus.api.CustomerDetails
import com.knoldus.command.{CreateCustomerCommand, DeleteCustomerCommand, GetCustomerCommand}
import com.knoldus.events.{CustomerAdded, CustomerDeleted, Events}
import com.knoldus.state.CustomerState
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.testkit.PersistentEntityTestDriver
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.Await
import scala.concurrent.duration._

class CustomerEntitySpec extends WordSpecLike with Matchers with BeforeAndAfterAll {

  val system = ActorSystem("CustomerEntitySpec", JsonSerializerRegistry.actorSystemSetupFor(CustomerSerializerRegistry))

  val customer = CustomerDetails("0101", "smith", "sm@gmail.com")

  override def afterAll(): Unit = Await.ready(system.terminate(), 10.seconds)

  "Customer Entity" must {
    "handle Create customer" in {
      val driver = new PersistentEntityTestDriver(system, new CustomerEntity, "1")
      val outcome: PersistentEntityTestDriver.Outcome[Events, CustomerState] = driver.run(CreateCustomerCommand(customer))
      outcome.events should contain only (CustomerAdded(customer))
      outcome.state should ===(CustomerState(Some(customer)))
    }

    "handle get Customer" in {
      val driver = new PersistentEntityTestDriver(system, new CustomerEntity, "1")
      driver.run(CreateCustomerCommand(customer))
      val outcome: PersistentEntityTestDriver.Outcome[Events, CustomerState] = driver.run(GetCustomerCommand("0101"))
      outcome.state should ===(CustomerState(Some(customer)))

    }

    "handle delete Customer" in {
      val driver = new PersistentEntityTestDriver(system, new CustomerEntity, "1")
      driver.run(CreateCustomerCommand(customer))
      val outcome: PersistentEntityTestDriver.Outcome[Events, CustomerState] = driver.run(DeleteCustomerCommand("0101"))
      outcome.events should contain(CustomerDeleted("0101"))
      outcome.state should ===(CustomerState(Some(customer)))

    }
  }

}
