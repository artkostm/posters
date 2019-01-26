package com.github.artkostm.models.definitions
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
case class Datetime(date: Option[java.time.OffsetDateTime] = None, period: Option[Period] = None)
object Datetime {
  implicit val encodeDatetime = {
    val readOnlyKeys = Set[String]()
    Encoder
      .forProduct2("date", "period") { (o: Datetime) =>
        (o.date, o.period)
      }
      .mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
  }
  implicit val decodeDatetime = Decoder.forProduct2("date", "period")(Datetime.apply _)
}
