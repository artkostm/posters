package com.artkostm.posters.worker.migration

import doobie._
import doobie.implicits._
import com.artkostm.posters.worker.DoobieMigration

class V0003__CreateEvents extends DoobieMigration {
  override def migrate: ConnectionIO[_] =
    sql"""
         CREATE TABLE IF NOT EXISTS events
         (
         	date timestamp NOT NULL
         		CONSTRAINT pk_events
         			PRIMARY KEY,
         	categories jsonb NOT NULL
         )
       """.update.run
}
