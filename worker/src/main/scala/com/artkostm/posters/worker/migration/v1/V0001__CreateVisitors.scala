package com.artkostm.posters.worker.migration.v1

import com.artkostm.posters.worker.migration.DoobieMigration
import doobie._
import doobie.implicits._

final class V0001__CreateVisitors extends DoobieMigration {
  override def migrate: ConnectionIO[_] =
    sql"""
         CREATE TABLE IF NOT EXISTS visitors
         (
            date timestamp NOT NULL,
            eventName varchar NOT NULL,
            vids text[] NOT NULL,
            uids text[] NOT NULL,
            CONSTRAINT pk_visitors
              PRIMARY KEY (date, eventName)
         )
    """.update.run
}
