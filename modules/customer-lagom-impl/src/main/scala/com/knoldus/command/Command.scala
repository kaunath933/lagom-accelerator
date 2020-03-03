package com.knoldus.command

import akka.Done
import com.knoldus.api.CustomerDetails
import play.api.libs.json.{Format, Json}

/**
 *A command in lagom is defined as a case class. For each command there is a command handler
 * that the persistent entity can receive.
 */

/**
 * create customer command
 *
 * @param customer -the customer object
 */
case class CreateCustomerCommand(customer: CustomerDetails) extends Commands[Done]

object CreateCustomerCommand {
  implicit val format: Format[CreateCustomerCommand] = Json.format
}

/**
 * GetCustomer command
 *
 * @param id - The customers id
 */
case class GetCustomerCommand(id: String) extends Commands[CustomerDetails]

object GetCustomerCommand {
  implicit val format: Format[GetCustomerCommand] = Json.format
}

/**
 * Delete customer command
 *
 * @param id  - the customers id
 */
case class DeleteCustomerCommand(id: String) extends Commands[Done]

object DeleteCustomerCommand {
  implicit val format: Format[DeleteCustomerCommand] = Json.format
}
