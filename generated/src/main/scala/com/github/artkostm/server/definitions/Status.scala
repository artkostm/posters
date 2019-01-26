package com.github.artkostm.server.definitions
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
case class Status(code: Int, errorType: String, webhookTimedOut: Option[BigDecimal] = None)
object Status {
  implicit val encodeStatus = {
    val readOnlyKeys = Set[String]()
    Encoder
      .forProduct3("code", "errorType", "webhookTimedOut") { (o: Status) =>
        (o.code, o.errorType, o.webhookTimedOut)
      }
      .mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
  }
  implicit val decodeStatus = Decoder.forProduct3("code", "errorType", "webhookTimedOut")(Status.apply _)
}
