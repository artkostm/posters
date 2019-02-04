package com.artkostm.posters.worker

import java.nio.charset.StandardCharsets
import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.{ChronoUnit, TemporalUnit}

import cats.data.NonEmptyList
import cats.effect._
import cats.implicits._
import com.artkostm.posters.interfaces.event.{Comment, EventData, EventInfo}
import com.artkostm.posters.interfaces.schedule._
import doobie._
import doobie.implicits._
import doobie.hikari._
import doobie.postgres._
import doobie.postgres.implicits._
import org.postgresql.util.PGobject
import com.github.plokhotnyuk.jsoniter_scala.core._
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import doobie.util.fragments

import scala.concurrent.ExecutionContext

object Sql extends App {

//  implicit def jsonbMeta[A: Manifest: JsonValueCodec]: doobie.Meta[A] =
//    doobie.Meta
//      .other[PGobject]("jsonb")
//      .xmap[A](
//        pgObject => readFromArray(pgObject.getValue.getBytes(StandardCharsets.UTF_8)),
//        jsonObject => {
//          val pgObject = new PGobject()
//          pgObject.setType("jsonb")
//          pgObject.setValue(new String(writeToArray(jsonObject), StandardCharsets.UTF_8))
//          pgObject
//        }
//      )
//
//  val transactor = HikariTransactor.newHikariTransactor[IO](driverClassName = "org.postgresql.Driver",
//                                                            url = "jdbc:postgresql://localhost:5432/postgres",
//                                                            user = "test",
//                                                            pass = "12345")
//
//  val date      = new Timestamp(Instant.now().toEpochMilli)
//  val eventName = "ko ko"
//  val vids      = Array("1", "3", "4")
//  val uids      = Array("5", "6", "7")
//
//  final case class Vis(date: Timestamp, event_name: String, vids: Array[String], uids: Array[String])
//
//  val eventInfo =
//    EventData("description", List("photo1", "photo2"), List(Comment("author", "today", "text", Some("raiting1"))))
//
//  final case class Vv(link: String, eventsInfo: EventData)
//
//  implicit val eventInfoJsonValueCodec = JsonCodecMaker.make[EventData](CodecMakerConfig())
//
////  val insert =
////    sql"""insert into visitors (date, event_name, vids, uids) values ($date, $eventName, $vids, $uids)""".update
////      .withUniqueGeneratedKeys[Vis]("date", "event_name", "vids", "uids")
//
//  val insert =
//    sql"""insert into info ("link", "eventsInfo") values ($eventName, $eventInfo)""".update
//      .withUniqueGeneratedKeys[Vv]("link", "eventsInfo")
//
//  val link = "ko ko"
//
//  val select = sql"""select "link", "eventsInfo" from info where link=$link"""
//    .query[EventInfo]
//    .option
//
//  val categs = List(
//    Category("name", List(Event("name", Media("link", "image"), Description("descr", Some("ticket"), true)))))
//
//  val dateT = Instant.now().truncatedTo(ChronoUnit.DAYS)
//
//  implicit val categJsonValueCodec = JsonCodecMaker.make[List[Category]](CodecMakerConfig())
//  implicit val catJsonValueCodec   = JsonCodecMaker.make[Category](CodecMakerConfig())
//
//  val insertCat =
//    sql"""insert into events ("date", "categories") values ($dateT, $categs)""".update
//      .withUniqueGeneratedKeys[Day]("date", "categories")
//
//  val selectCategories = sql"""select "date", "categories" from events where "date"=$dateT"""
//    .query[Day]
//    .option
//
//  val sc = (
//    fr"""SELECT j FROM events t, jsonb_array_elements(t.categories) j WHERE """ ++ fragments.in(
//      fr"""j->>'name'""",
//      NonEmptyList.fromListUnsafe(List("name1", "name")))
//  ).query[Category]
//    .to[List]
//
//  (for {
//    xa <- transactor
//  } yield {
//    val y = xa.yolo
//    import y._
//    val vis = sc.transact(xa).unsafeRunSync()
//    println(vis)
//  }).unsafeRunSync()
}
