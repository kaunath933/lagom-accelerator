package com.knoldus.api

import play.api.libs.json.{Format, Json}

/**
 *
 * @param id    - The customers id
 * @param name  - The customers name
 * @param email - The customers email
 */
case class CustomerDetails(id: String, name: String, email: String)

object CustomerDetails {

  implicit val format: Format[CustomerDetails] = Json.format[CustomerDetails]
}

