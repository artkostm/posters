package com.artkostm.posters.algebra

import java.time.{Instant, LocalDate}

import com.artkostm.posters.interfaces.intent.{Intent, Intents}

trait VisitorStore[F[_]] {
  def deleteOlderThan(today: LocalDate): F[Int]
  def save(intent: Intent): F[Intents]
  def find(date: LocalDate, eventName: String): F[Option[Intents]]
  def asVolunteer(intent: Intent): F[Intents]
  def asPlainUser(intent: Intent): F[Intents]
  def leave(intent: Intent): F[Intents]
}

//object testJson extends App {
//  println(Instant.now)
//  import com.github.plokhotnyuk.jsoniter_scala.core._
//  import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
//
//  implicit val intentCodec: JsonValueCodec[Intent] = JsonCodecMaker.make[Intent](CodecMakerConfig())
//
//  val intent: Intent = readFromArray(
//    """
//      |{"date":"2019-02-19T13:56:24.678Z", "eventName":"event name","userId":"2"}
//    """.stripMargin.getBytes)
//
//  println(intent)
//
//  println(new String(writeToArray(Intent(Instant.now, "event name", "1"))))
//}
