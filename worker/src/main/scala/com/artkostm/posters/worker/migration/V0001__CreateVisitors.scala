package com.artkostm.posters.worker.migration

import doobie._
import doobie.implicits._
import com.artkostm.posters.worker.DoobieMigration

final class V0001__CreateVisitors extends DoobieMigration {
  override def migrate: ConnectionIO[_] =
    sql"""
         CREATE TABLE IF NOT EXISTS visitors
         (
            date timestamp NOT NULL,
            event_name varchar NOT NULL,
            vids text NOT NULL,
            uids text NOT NULL,
            CONSTRAINT pk_visitors
              PRIMARY KEY (date, event_name)
         )
    """.update.run
}
