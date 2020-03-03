package com.knoldus.events

import akka.Done
import com.datastax.driver.core.{BoundStatement, PreparedStatement}
import com.knoldus.Constants.QueryConstants
import com.knoldus.api.CustomerDetails
import com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}

import scala.concurrent.{ExecutionContext, Future}

/**
 * The CustomerEventReadSideProcessor class is used to implement readside processor by implementing the trait EventReadSideProcessor
 * @param db        -The Cassandra Session
 * @param readSide  -The Cassandra Readside
 * @param ec        - The Execution context
 */

case class CustomerEventReadSideProcessor(db: CassandraSession, readSide: CassandraReadSide)(implicit ec: ExecutionContext) extends EventReadSideProcessor {

  var addEntity: PreparedStatement = _

  var deleteEntity: PreparedStatement = _

  /**
   * This method on the ReadSideProcessor is buildHandler.
   * This is responsible for creating the ReadSideHandler that will handle events
   * @return
   */
  override def buildHandler(): ReadSideProcessor.ReadSideHandler[Events] = readSide.builder[Events]("EventReadSidePreocessor")
    .setGlobalPrepare(() => createTable)
    .setPrepare(_ => prepareStatements())
    .setEventHandler[CustomerAdded](ese => addEntity(ese.event.customer))
    .setEventHandler[CustomerDeleted](ese => deleteEntity(ese.event.id))
    .build()

  /**
   * Creates a table at the start up of the application.
   *
   * @return Future[Done]
   */
  override def createTable(): Future[Done] = {
    db.executeCreateTable(QueryConstants.CREATE_TABLE
      .stripMargin)
  }

  /**
   * This will be executed once per shard, when the read side processor starts up.
   * It can be used for preparing statements in order to optimize Cassandra’s handling of them.
   *
   * @return Future[Done]
   */
  def prepareStatements(): Future[Done] =
    db.prepare(QueryConstants.INSERT_PRODUCT)
      .map { ps =>
        addEntity = ps
        Done
      }.map(_ => db.prepare(QueryConstants.DELETE_PRODUCT).map(ps => {
      deleteEntity = ps
      Done
    })).flatten

  def addEntity(entity: CustomerDetails): Future[List[BoundStatement]] = {
    val bindInsertCustomer: BoundStatement = addEntity.bind()
    bindInsertCustomer.setString("id", entity.id)
    bindInsertCustomer.setString("name", entity.name)
    bindInsertCustomer.setString("email", entity.email)
    Future.successful(List(bindInsertCustomer))
  }

  def deleteEntity(entity: String): Future[List[BoundStatement]] = {
    val bindDeleteCustomer: BoundStatement = deleteEntity.bind()
    bindDeleteCustomer.setString("id", entity)
    Future.successful(List(bindDeleteCustomer))
  }
}
