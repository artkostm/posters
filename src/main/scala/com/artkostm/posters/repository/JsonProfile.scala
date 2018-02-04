package com.artkostm.posters.repository

import com.artkostm.posters.model.{Category, EventInfo}
import com.github.tminglei.slickpg.PgPlayJsonSupport
import com.github.tminglei.slickpg.array.PgArrayJdbcTypes
import com.github.tminglei.slickpg.utils.SimpleArrayUtils
import org.joda.time.DateTime
import play.api.libs.json.{JsValue, Json}
import slick.jdbc.PostgresProfile

trait JsonSupportPostgresProfile extends PostgresProfile
                               with PgPlayJsonSupport
                               with PgArrayJdbcTypes {

  override def pgjson = "jsonb"

  override val api: API = new PostersDBAPI {}

  val plainAPI = new PostersDBAPI with PlayJsonPlainImplicits

  trait PostersDBAPI extends super.API with JsonImplicits {
    import Category._
    import EventInfo._
    implicit val categoryJsonTypeMapper = MappedJdbcType.base[Category, JsValue](Json.toJson(_), _.as[Category])
    implicit val eventInfoJsonTypeMapper = MappedJdbcType.base[EventInfo, JsValue](Json.toJson(_), _.as[EventInfo])
    implicit val categoryArrayTypeMapper = MappedJdbcType.base[List[Category], JsValue](Json.toJson(_), _.as[List[Category]])
    implicit val eventInfoArrayTypeMapper = new AdvancedArrayJdbcType[EventInfo](pgjson,
      (s) => SimpleArrayUtils.fromString[EventInfo](Json.parse(_).as[EventInfo])(s).orNull,
      (v) => SimpleArrayUtils.mkString[EventInfo](c => Json.toJson(c).toString)(v)).to(_.toList)
    implicit val dateTime2SqlTimestampMapper = MappedColumnType.base[DateTime, java.sql.Timestamp](
      date => new java.sql.Timestamp(date.toDate.getTime),
      sqlTimestamp => new DateTime(sqlTimestamp.getTime()))
  }
}

object JsonSupportPostgresProfile extends JsonSupportPostgresProfile
