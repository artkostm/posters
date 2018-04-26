package com.artkostm.posters.repository

import com.github.tminglei.slickpg.{ExPostgresProfile, PgArraySupport, PgDate2Support, PgPlayJsonSupport}
import org.joda.time.DateTime

trait PostersPgProfile extends ExPostgresProfile
  with PgArraySupport
  with PgDate2Support
  with PgPlayJsonSupport{

  override def pgjson: String = "jsonb"

  override val api: API = new API
    with ArrayImplicits
    with DateTimeImplicits
    with PlayJsonImplicits {
    implicit val dateTime2SqlTimestampMapper = MappedColumnType.base[DateTime, java.sql.Timestamp](
      date => new java.sql.Timestamp(date.toDate.getTime),
      sqlTimestamp => new DateTime(sqlTimestamp.getTime()))
  }
}

object PostersPgProfile extends PostersPgProfile {}