package com.artkostm.posters.worker.migration.v1

import com.artkostm.posters.worker.migration.DoobieMigration
import doobie._
import doobie.implicits._

final class V0002__CreateInfo extends DoobieMigration {
  override def migrate: ConnectionIO[_] =
    sql"""
         CREATE TABLE IF NOT EXISTS info
         (
            link VARCHAR NOT NULL
              CONSTRAINT pk_info
                PRIMARY KEY,
            "eventInfo" JSONB NOT NULL
         )
    """.update.run
}
