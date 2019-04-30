package com.artkostm.posters.endpoint

import java.time.LocalDate

import cats.data.NonEmptyList
import cats.effect.IO
import com.artkostm.posters.algebra.EventStore
import com.artkostm.posters.endpoint.error.HttpErrorHandler
import com.artkostm.posters.interfaces.auth.User
import com.artkostm.posters.interfaces.dialog.v1.DialogflowRequest
import com.artkostm.posters.interfaces.schedule.{Category, Day}
import com.artkostm.posters.service.DfWebhookService
import com.artkostm.posters.jsoniter._
import com.artkostm.posters.jsoniter.codecs._
import org.http4s.{AuthedRequest, Method, Request, Uri}
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class DfV1EndpointTest
    extends FlatSpec
    with Matchers
    with MockFactory
    with ScalaCheckPropertyChecks
    with Http4sDsl[IO]
    with PostersArbitraries {

  implicit val errorHandler = new HttpErrorHandler[IO]

  "test" should "pass" in {
    val eventStore = mock[EventStore[IO]]

    val webhookService = new DfWebhookService[IO](eventStore)

    val endpoint = new DfWebhookEndpoint[IO](webhookService)

    forAll { (dfRequest: DialogflowRequest, user: User) =>
      givenEventStoreReturnsCorrectData(eventStore)
      (
        for {
          response <- endpoint.endpoints.orNotFound(
                       AuthedRequest(
                         user,
                         Request[IO](Method.POST,
                                     Uri.unsafeFromString(s"/v1/webhook"),
                                     body = jsoniterEntityEncoder[IO, DialogflowRequest].toEntity(dfRequest).body)))
        } yield {
          println(new String(response.body.compile.toList.unsafeRunSync().toArray))
          response.status shouldEqual Ok
        }
      ).unsafeRunSync()
    }
  }

  def givenEventStoreReturnsCorrectData(store: EventStore[IO]) =
    inAnyOrder {
      (store.findByNamesAndDate _) expects (*, *) returning IO.pure(List.empty[Category]) anyNumberOfTimes()

      (store.findByDate _) expects (*) returning IO.pure(Some(Day(LocalDate.now, List()))) anyNumberOfTimes ()

      (store.findByPeriod _) expects (*, *) returning IO.pure(List.empty[Day]) anyNumberOfTimes ()

      (store.findByNamesAndPeriod _) expects (*, *, *) returning IO.pure(List.empty[Category]) anyNumberOfTimes ()
    }
}
