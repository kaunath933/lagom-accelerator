package com.knoldus.Constants

import com.typesafe.config._

object CreateTable {
  val config = ConfigFactory.load()
  val KEYSPACE_NAME = config.getString("user.cassandra.keyspace")
  val TABLE_NAME = config.getString("cassandra.tableName")
}
