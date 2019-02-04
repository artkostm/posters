package com.github.artkostm.models.definitions
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
case class Comment(author: String, date: String, text: String)
object Comment {
  implicit val encodeComment = {
    val readOnlyKeys = Set[String]()
    Encoder
      .forProduct3("author", "date", "text") { (o: Comment) =>
        (o.author, o.date, o.text)
      }
      .mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
  }
  implicit val decodeComment = Decoder.forProduct3("author", "date", "text")(Comment.apply _)
}
