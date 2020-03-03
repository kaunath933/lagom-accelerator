package com.knoldus

import akka.Done
import com.knoldus.api.CustomerDetails
import com.knoldus.command.{Commands, CreateCustomerCommand, DeleteCustomerCommand, GetCustomerCommand}
import com.knoldus.events.{CustomerAdded, CustomerDeleted, Events}
import com.knoldus.state.CustomerState
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

/**
 * To create a persistent entity, you should extend the PersisttentEntity trait and override Command, Event,State and initialState
 * with your command class, event class ,state class and initial state respectively.
 */
class CustomerEntity extends PersistentEntity {

  override type Command = Commands[_]
  override type Event = Events
  override type State = CustomerState


  override def initialState = CustomerState(None)

  /**
   * behavior is an abstract method that your concrete subclass must implement.
   * Behavior is a function from current State to Actions,which defines command and event handlers
   * Command handlers are invoked for incoming messages
   *
   * @return State=>Actions
   */
  override def behavior: (CustomerState) => Actions = {
    case CustomerState(_) => Actions()
      .onCommand[CreateCustomerCommand, Done]                        //Command handlers are invoked for incoming messages
        {                                                            //Command handler returns a persist directive which shows the events
        case (CreateCustomerCommand(cust), ctx, _) =>                //the events to be persisted
          ctx.thenPersist(CustomerAdded(cust))(_ ⇒ ctx.reply(Done))  //thenPersist will persist one single event
      }
      .onReadOnlyCommand[GetCustomerCommand, CustomerDetails] {      //The onReadOnlyCommand of the Actions do not change application state.
        case (GetCustomerCommand(id), ctx, state) =>
          ctx.reply(state.customer.getOrElse(CustomerDetails(id, "name not found ", "email not found")))
      }
      .onEvent {                                                      //When an event has been persisted successfully the current state is updated by applying
        case (CustomerAdded(customer), _) =>                          //the event to the current state. The functions for updating the state are registered with
          CustomerState(Some(customer))                               // the onEvent method of the Actions.
      }
      .onCommand[DeleteCustomerCommand, Done] {
        case (DeleteCustomerCommand(id), ctx, _) => {
          val event = CustomerDeleted(id)

          ctx.thenPersist(event) { _ =>
            ctx.reply(Done)
          }
        }
      }.onEvent {
      case (_, state) =>
        state
    }
  }
}

/**
 *The need to implement JsonSerializerRegistry is to have all the
 * service formats returned from its serializers method.
 */
object CustomerSerializerRegistry extends JsonSerializerRegistry {
  override def serializers = List(
    JsonSerializer[CustomerDetails],

    //Commands
    JsonSerializer[CreateCustomerCommand],
    JsonSerializer[GetCustomerCommand],
    JsonSerializer[DeleteCustomerCommand],

    //Events
    JsonSerializer[CustomerAdded],
    JsonSerializer[CustomerDeleted],

    //state
    JsonSerializer[CustomerState]
  )

}
