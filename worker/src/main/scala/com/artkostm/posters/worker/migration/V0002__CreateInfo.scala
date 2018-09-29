package com.artkostm.posters.worker.migration

import doobie._
import doobie.implicits._
import com.artkostm.posters.worker.DoobieMigration

final class V0002__CreateInfo extends DoobieMigration {
  override def migrate: ConnectionIO[_] =
    sql"""
         CREATE TABLE IF NOT EXISTS info
         (
            link varchar NOT NULL
              CONSTRAINT pk_info
                PRIMARY KEY,
            "eventsInfo" varchar NOT NULL
         )
    """.update.run

}
