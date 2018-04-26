package com.artkostm.posters.repository

import com.github.tminglei.slickpg.{ExPostgresProfile, PgArraySupport, PgDate2Support, PgPlayJsonSupport}

trait PostersPgProfile extends ExPostgresProfile
  with PgArraySupport
  with PgDate2Support
  with PgPlayJsonSupport{

  override def pgjson: String = "jsonb"

  override override val api: API = new API
    with ArrayImplicits
    with DateTimeImplicits
    with PlayJsonImplicits {}
}
