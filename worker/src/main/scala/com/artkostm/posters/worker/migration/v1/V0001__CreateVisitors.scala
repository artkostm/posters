package com.artkostm.posters.worker.migration.v1

import com.artkostm.posters.worker.migration.DoobieMigration
import doobie._
import doobie.implicits._

final class V0001__CreateVisitors extends DoobieMigration {
  override def migrate: ConnectionIO[_] =
    sql"""
         CREATE TABLE IF NOT EXISTS visitors
         (
            eventdate DATE NOT NULL,
            eventname VARCHAR NOT NULL,
            vids TEXT[] NOT NULL,
            uids TEXT[] NOT NULL,
            CONSTRAINT pk_visitors
              PRIMARY KEY (eventdate, eventname)
         )
    """.update.run
}
