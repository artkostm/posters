package com.github.artkostm.server.definitions
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
case class Message(`type`: Int, speech: String)
object Message {
  implicit val encodeMessage = {
    val readOnlyKeys = Set[String]()
    Encoder
      .forProduct2("type", "speech") { (o: Message) =>
        (o.`type`, o.speech)
      }
      .mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
  }
  implicit val decodeMessage = Decoder.forProduct2("type", "speech")(Message.apply _)
}
