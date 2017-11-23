package com.artkostm.posters.model

import java.util.Date

import slick.jdbc.H2Profile.api._
import slick.lifted.ProvenShape

class Events(tag: Tag) extends Table[(String, Date, String, String)](tag, "events") {
  implicit val d2t = DateMapper.DateTime2SqlTimestampMapper
  def category: Rep[String] = column[String]("category")
  def date: Rep[Date] = column[Date]("date")
  def eventName: Rep[String] = column[String]("event_date")
  def ids: Rep[String] = column[String]("ids")

  def * : ProvenShape[(String, Date, String, String)] = (category, date, eventName, ids)
  def pk = primaryKey("pk_events", (category, date, eventName))
}

object DateMapper {
  val DateTime2SqlTimestampMapper = MappedColumnType.base[Date, java.sql.Timestamp](
    { utilDate => new java.sql.Timestamp(utilDate.getTime) },
    { sqlTimestamp => new Date(sqlTimestamp.getTime()) })
}
