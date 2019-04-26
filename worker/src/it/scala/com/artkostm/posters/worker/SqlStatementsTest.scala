package com.artkostm.posters.worker

import java.time.LocalDate.now

import cats.Monad
import cats.data.NonEmptyList
import cats.effect.{Clock, Concurrent, ContextShift, IO}
import cats.implicits._
import cats.instances.option._
import com.artkostm.posters.interfaces.event.{EventData, EventInfo}
import com.artkostm.posters.interfaces.intent.Intent
import com.artkostm.posters.interfaces.schedule.Day
import com.dimafeng.testcontainers.{Container, ForAllTestContainer, PostgreSQLContainer}
import com.artkostm.posters.interpreter.{EventStoreInterpreter => ES, InfoStoreInterpreter => IS, VisitorStoreInterpreter => VS}
import doobie.util.yolo.Yolo
import org.scalatest.FlatSpec

import scala.concurrent.ExecutionContext

class SqlStatementsTest extends FlatSpec with ForAllTestContainer{

  override val container: Container = PostgreSQLContainer().configure { provider =>
    provider.withUsername("test")
    provider.withPassword("12345")
    provider.withDatabaseName("postgres")
  }


  "" should "" in {
    WorkerModule.init[IO].map(_.xa.yolo).use { implicit yolo =>
      for {
        _ <- SqlStatementsTest.checkEventStore[IO]
        _ <- SqlStatementsTest.checkInfoStore[IO]
        _ <- SqlStatementsTest.checkVisitorStore[IO]
      } yield ()
    }.unsafeRunSync()
  }

}

object SqlStatementsTest {
  def checkEventStore[F[_]: Monad](implicit y: Yolo[F]): F[Unit] = {
    import y._
    for {
      _ <- ES.deleteOldEvents(now()).check
      _ <- ES.findByDay(now()).check
      _ <- ES.findCategoriesByNamesAndDate(NonEmptyList.of("Party"), now()).check
      _ <- ES.findCategoriesByNames(NonEmptyList.of("Party")).check
      _ <- ES.findCategoriesByNamesAndPeriod(NonEmptyList.of("Party"), now(), now.plusDays(1)).check
      _ <- ES.saveEvents(Day(now, List())).check
      _ <- ES.findByDays(now(), now.plusDays(1)).check
    } yield ()
  }

  def checkInfoStore[F[_]: Monad](implicit y: Yolo[F]): F[Unit] = {
    import y._
    for {
      _ <- IS.deleteOldInfo().check
      _ <- IS.findByLink("link").check
      _ <- IS.saveInfo(EventInfo("link", EventData("description", List(), List()))).check
    } yield ()
  }

  def checkVisitorStore[F[_]: Monad](implicit y: Yolo[F]): F[Unit] = {
    import y._
    for {
      _ <- VS.findByDateAndName(now(), "McGiver").check
      _ <- VS.saveIntent(Intent(now().minusDays(4), "testEvent", "12")).check
      _ <- VS.deleteOldIntents(now()).check
      _ <- VS.leaveEvent(Intent(now().minusDays(4), "testEvent", "12")).check
      _ <- VS.volunteer(Intent(now().minusDays(4), "testEvent", "12")).check
      _ <- VS.plainUser(Intent(now().minusDays(4), "testEvent", "12")).check
    } yield ()
  }
}