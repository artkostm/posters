package com.github.artkostm.server.definitions
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
case class Period(endDate: java.time.OffsetDateTime, startDate: java.time.OffsetDateTime)
object Period {
  implicit val encodePeriod = {
    val readOnlyKeys = Set[String]()
    Encoder
      .forProduct2("endDate", "startDate") { (o: Period) =>
        (o.endDate, o.startDate)
      }
      .mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
  }
  implicit val decodePeriod = Decoder.forProduct2("endDate", "startDate")(Period.apply _)
}
