package com.knoldus.events

import com.knoldus.api.CustomerDetails
import play.api.libs.json.{Format, Json}

/**
 *An event in lagom is defined as a case class. For each command there is a command handler
 * that processes the command and persists events according to it.
 */

/**
 * The CustomerAdded event
 *
 * @param customer The customer object
 */
case class CustomerAdded(customer: CustomerDetails) extends Events

object CustomerAdded {
  implicit val format: Format[CustomerAdded] = Json.format
}

/**
 * The CustomerDeleted Event
 *
 * @param id  - The  Cusotmers id
 */
case class CustomerDeleted(id: String) extends Events

object CustomerDeleted {
  implicit val format: Format[CustomerDeleted] = Json.format
}
