package com.artkostm.posters.repository

import com.artkostm.posters.model.{Category, EventInfo, Test}
import com.github.tminglei.slickpg.utils.SimpleArrayUtils
import com.github.tminglei.slickpg.{ExPostgresProfile, PgArraySupport, PgDate2Support, PgPlayJsonSupport}
import org.joda.time.DateTime
import play.api.libs.json.{JsValue, Json}

trait PostersPgProfile extends ExPostgresProfile
  with PgArraySupport
  with PgDate2Support
  with PgPlayJsonSupport {

  override def pgjson: String = "jsonb"

  override val api = new PostersAPI {}

  trait PostersAPI extends API
    with ArrayImplicits
    with DateTimeImplicits
    with PlayJsonImplicits {
    implicit val dateTime2SqlTimestampMapper = MappedColumnType.base[DateTime, java.sql.Timestamp](
      date => new java.sql.Timestamp(date.toDate.getTime),
      sqlTimestamp => new DateTime(sqlTimestamp.getTime()))

    implicit val strListTypeMapper = new SimpleArrayJdbcType[String]("text").to(_.toList)

    import Category._
    import EventInfo._

    implicit val categoryJsonTypeMapper = MappedJdbcType.base[Category, JsValue](Json.toJson(_), _.as[Category])

    implicit val eventInfoJsonTypeMapper = MappedJdbcType.base[EventInfo, JsValue](Json.toJson(_), _.as[EventInfo])

    implicit val categoryArrayTypeMapper = MappedJdbcType.base[List[Category], JsValue](Json.toJson(_), _.as[List[Category]])

    implicit val eventInfoArrayTypeMapper = new AdvancedArrayJdbcType[EventInfo](pgjson,
      (s) => SimpleArrayUtils.fromString[EventInfo](Json.parse(_).as[EventInfo])(s).orNull,
      (v) => SimpleArrayUtils.mkString[EventInfo](c => Json.toJson(c).toString)(v)).to(_.toList)

    import com.artkostm.posters.model.Testdd._
    implicit val testJsonTypeMapper = MappedJdbcType.base[Test, JsValue](Json.toJson(_), _.as[Test])
  }
}

object PostersPgProfile extends PostersPgProfile