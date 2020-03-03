package com.knoldus.state

import com.knoldus.api.CustomerDetails
import play.api.libs.json.{Format, Json}

case class CustomerState(customer: Option[CustomerDetails])

object CustomerState {
  implicit val format: Format[CustomerState] = Json.format
}
