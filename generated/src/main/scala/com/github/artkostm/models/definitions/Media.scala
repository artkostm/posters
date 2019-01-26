package com.github.artkostm.models.definitions
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
case class Media(link: String, img: String)
object Media {
  implicit val encodeMedia = {
    val readOnlyKeys = Set[String]()
    Encoder
      .forProduct2("link", "img") { (o: Media) =>
        (o.link, o.img)
      }
      .mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
  }
  implicit val decodeMedia = Decoder.forProduct2("link", "img")(Media.apply _)
}
