package com.knoldus.command

import akka.Done
import com.knoldus.api.CustomerDetails
import play.api.libs.json.{Format, Json}

case class CreateCustomerCommand(customer: CustomerDetails) extends Commands[Done]

object CreateCustomerCommand {
  implicit val format: Format[CreateCustomerCommand] = Json.format
}

case class GetCustomerCommand(id: String) extends Commands[CustomerDetails]

object GetCustomerCommand {
  implicit val format: Format[GetCustomerCommand] = Json.format
}

case class DeleteCustomerCommand(id: String) extends Commands[Done]

object DeleteCustomerCommand {
  implicit val format: Format[DeleteCustomerCommand] = Json.format
}
