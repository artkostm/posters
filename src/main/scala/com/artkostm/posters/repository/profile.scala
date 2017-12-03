package com.artkostm.posters.repository

import com.artkostm.posters.model.{Category, EventInfo}
import com.github.tminglei.slickpg.PgPlayJsonSupport
import com.github.tminglei.slickpg.array.PgArrayJdbcTypes
import com.github.tminglei.slickpg.utils.SimpleArrayUtils
import play.api.libs.json.{JsValue, Json}
import slick.jdbc.PostgresProfile

trait HerokuPostgresProfile extends PostgresProfile
                               with PgPlayJsonSupport
                               with PgArrayJdbcTypes {

  override val pgjson = "jsonb"

  override val api: API = new PostersDBAPI {}

  val plainAPI = new PostersDBAPI with PlayJsonPlainImplicits

  trait PostersDBAPI extends super.API with JsonImplicits {
    import Category._
    import EventInfo._
    implicit val categoryJsonTypeMapper = MappedJdbcType.base[Category, JsValue](Json.toJson(_), _.as[Category])
    implicit val eventInfoJsonTypeMapper = MappedJdbcType.base[EventInfo, JsValue](Json.toJson(_), _.as[EventInfo])
    implicit val categoryArrayTypeMapper = new AdvancedArrayJdbcType[Category](pgjson,
      (s) => SimpleArrayUtils.fromString[Category](Json.parse(_).as[Category])(s).orNull,
      (v) => SimpleArrayUtils.mkString[Category](c => Json.stringify(Json.toJson(c)))(v)).to(_.toList)
    implicit val eventInfoArrayTypeMapper = new AdvancedArrayJdbcType[EventInfo](pgjson,
      (s) => SimpleArrayUtils.fromString[EventInfo](Json.parse(_).as[EventInfo])(s).orNull,
      (v) => SimpleArrayUtils.mkString[EventInfo](c => Json.stringify(Json.toJson(c)))(v)).to(_.toList)
  }
}

object HerokuPostgresProfile extends HerokuPostgresProfile
