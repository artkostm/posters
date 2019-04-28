package com.artkostm.posters.endpoint

import cats.effect.IO
import com.artkostm.posters.algebra.InfoStore
import com.artkostm.posters.endpoint.error.HttpErrorHandler
import com.artkostm.posters.interfaces.auth.User
import com.artkostm.posters.interfaces.event.{EventData, EventInfo}
import org.http4s.dsl.Http4sDsl
import org.http4s.{AuthedRequest, Method, Request, Response, Uri}
import org.http4s.implicits._
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Assertion, FlatSpec, Matchers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class InfoEndpointTest
    extends FlatSpec
    with Matchers
    with MockFactory
    with ScalaCheckPropertyChecks
    with Http4sDsl[IO]
    with PostersArbitraries {

  implicit val errorHandler = new HttpErrorHandler[IO]

  def withEndpointSetup(repoMock: InfoStore[IO] => Unit)(checkResponse: Response[IO] => Assertion): Unit = {
    val infoRepo = mock[InfoStore[IO]]

    val infoEndpoint = new InfoEndpoint[IO](infoRepo)

    forAll { (link: Link, user: User) =>
      repoMock(infoRepo)
      (
        for {
          response <- infoEndpoint.endpoints.orNotFound(
                       AuthedRequest(user,
                                     Request[IO](Method.GET, Uri.unsafeFromString(s"/v1/events?link=${link.value}"))))
        } yield checkResponse(response)
      ).unsafeRunSync()
    }
  }

  "InfoEndpoint" should "return event info by link with 200 status code" in withEndpointSetup(
    givenInfoStoreReturnsEventInfo) { response =>
    response.status shouldEqual Ok
  }

  "InfoEndpoint" should "return error for the link that does not exist" in withEndpointSetup(givenInfoStoreReturnsNone) {
    response =>
      response.status shouldEqual NotFound
  }

  def givenInfoStoreReturnsEventInfo(store: InfoStore[IO]) =
    (store.find _) expects (*) onCall { l: String =>
      IO.pure(Some(EventInfo(l, EventData("description", List(), List()))))
    }

  def givenInfoStoreReturnsNone(store: InfoStore[IO]) =
    (store.find _) expects (*) onCall { _: String =>
      IO.pure(None)
    }
}
