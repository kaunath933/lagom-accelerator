package com.knoldus.Constants

import com.typesafe.config.ConfigFactory

object QueryConstants {

  val config = ConfigFactory.load()

  val CREATE_TABLE =
    s"""CREATE TABLE IF NOT EXISTS ${CreateTable.KEYSPACE_NAME}.${CreateTable.TABLE_NAME} (
       |id text PRIMARY KEY, name text, email text)"""

  // Get the details of a customer from a database by customer id.
  val GET_PRODUCT = s"SELECT * FROM ${CreateTable.TABLE_NAME} WHERE id =?"

  // Get the details of all customer from database.
  val GET_ALL_PRODUCTS = s"SELECT * FROM ${CreateTable.TABLE_NAME}"

  //Insert a customer details into database
  val INSERT_PRODUCT = s"INSERT INTO ${CreateTable.KEYSPACE_NAME}.${CreateTable.TABLE_NAME} (id, name, email) VALUES (?, ?, ?)"

  //Delete a customer from a database by id
  val DELETE_PRODUCT = s"DELETE FROM ${CreateTable.TABLE_NAME}  where id = ?"
}
