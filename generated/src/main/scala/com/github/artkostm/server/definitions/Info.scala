package com.github.artkostm.server.definitions
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
case class Info(link: String, eventInfo: EventInfo)
object Info {
  implicit val encodeInfo = {
    val readOnlyKeys = Set[String]()
    Encoder.forProduct2("link", "eventInfo") { (o: Info) => (o.link, o.eventInfo) }.mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
  }
  implicit val decodeInfo = Decoder.forProduct2("link", "eventInfo")(Info.apply _)
}