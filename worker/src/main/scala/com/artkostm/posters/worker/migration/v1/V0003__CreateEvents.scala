package com.artkostm.posters.worker.migration.v1

import com.artkostm.posters.worker.migration.DoobieMigration
import doobie._
import doobie.implicits._

class V0003__CreateEvents extends DoobieMigration {
  override def migrate: ConnectionIO[_] =
    sql"""
         CREATE TABLE IF NOT EXISTS events
         (
         	eventdate DATE NOT NULL
         		CONSTRAINT pk_events
         			PRIMARY KEY,
         	categories JSONB NOT NULL
         )
       """.update.run
}
