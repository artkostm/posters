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
import org.http4s.{AuthedRequest, Method, Request, Response, Uri}
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

  "Dialogflow Endpoint V1" should "return correct result" in {
    val eventStore     = mock[EventStore[IO]]
    val webhookService = new DfWebhookService[IO](eventStore)
    val endpoint       = new DfWebhookEndpoint[IO](webhookService)

    forAll { (dfRequest: DialogflowRequest, user: User) =>
      givenEventStoreReturnsCorrectData(eventStore)
      callEndpoint(endpoint, dfRequest, user).map(_.status shouldEqual Ok).unsafeRunSync()
    }
  }

  "Dialogflow Endpoint V1" should "return error on incomplete action" in {
    val eventStore     = mock[EventStore[IO]]
    val webhookService = new DfWebhookService[IO](eventStore)
    val endpoint       = new DfWebhookEndpoint[IO](webhookService)

    forAll { (dfRequest: DialogflowRequest, user: User) =>
      givenEventStoreReturnsCorrectData(eventStore)
      val request = dfRequest.copy(result = dfRequest.result.copy(actionIncomplete = true))
      callEndpoint(endpoint, request, user).map(_.status shouldEqual BadRequest).unsafeRunSync()
    }
  }

  "Dialogflow Endpoint V1" should "return error when data and period are not present" in {
    val eventStore     = mock[EventStore[IO]]
    val webhookService = new DfWebhookService[IO](eventStore)
    val endpoint       = new DfWebhookEndpoint[IO](webhookService)

    forAll { (dfRequest: DialogflowRequest, user: User) =>
      givenEventStoreReturnsCorrectData(eventStore)
      val request = dfRequest.copy(
        result = dfRequest.result.copy(parameters =
          dfRequest.result.parameters.copy(datetime = dfRequest.result.parameters.datetime.copy(None, None))))
      callEndpoint(endpoint, request, user).map(_.status shouldEqual BadRequest).unsafeRunSync()
    }
  }

  def givenEventStoreReturnsCorrectData(store: EventStore[IO]) =
    inAnyOrder {
      (store.findByNamesAndDate _) expects (*, *) returning IO.pure(List.empty[Category]) anyNumberOfTimes ()
      (store.findByDate _) expects (*) returning IO.pure(Some(Day(LocalDate.now, List()))) anyNumberOfTimes ()
      (store.findByPeriod _) expects (*, *) returning IO.pure(List.empty[Day]) anyNumberOfTimes ()
      (store.findByNamesAndPeriod _) expects (*, *, *) returning IO.pure(List.empty[Category]) anyNumberOfTimes ()
    }

  def callEndpoint(endpoint: DfWebhookEndpoint[IO], request: DialogflowRequest, user: User): IO[Response[IO]] =
    endpoint.endpoints.orNotFound(
      AuthedRequest(user,
                    Request[IO](Method.POST,
                                Uri.unsafeFromString(s"/v1/webhook"),
                                body = jsoniterEntityEncoder[IO, DialogflowRequest].toEntity(request).body)))
}
