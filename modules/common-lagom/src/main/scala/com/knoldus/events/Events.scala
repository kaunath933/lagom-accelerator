package com.knoldus.events

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventShards, AggregateEventTag, AggregateEventTagger}
import com.typesafe.config.ConfigFactory

trait Events extends AggregateEvent[Events] {
  override def aggregateTag: AggregateEventTagger[Events] = Events.Tag
}

object Events {
  val config = ConfigFactory.load

  val NumShards = config.getInt("cassandra.numShards")
  val Tag: AggregateEventShards[Events] = AggregateEventTag.sharded[Events](NumShards)
}
