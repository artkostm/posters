package com.artkostm.posters.repository

import slick.basic.{BasicProfile, DatabaseConfig}

trait HasDatabaseConfig[P <: BasicProfile] {

  protected val dbConfig: DatabaseConfig[P]

  protected final lazy val profile: P = dbConfig.profile

  protected final def db: P#Backend#Database = dbConfig.db
}