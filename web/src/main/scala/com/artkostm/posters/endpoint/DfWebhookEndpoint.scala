package com.artkostm.posters.endpoint

import cats.implicits._
import cats.effect.Effect
import com.artkostm.posters.endpoint.error.HttpErrorHandler
import com.artkostm.posters.jsoniter._
import com.artkostm.posters.jsoniter.codecs._
import com.artkostm.posters.interfaces.auth.User
import com.artkostm.posters.interfaces.dialog.ResponsePayload
import com.artkostm.posters.interfaces.dialog.v1.{DialogflowRequest => Request1, DialogflowResponse => Response1}
import com.artkostm.posters.interfaces.dialog.v2.{DialogflowRequest => Request2, DialogflowResponse => Response2}
import com.artkostm.posters.service.{DfKeyData, DfWebhookService}
import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import org.http4s.AuthedService
import org.http4s.dsl.Http4sDsl

class DfWebhookEndpoint[F[_]: Effect](service: DfWebhookService[F])(implicit H: HttpErrorHandler[F])
    extends Http4sDsl[F]
    with EndpointsAware[F] {

  private def versioned[Req: DfKeyData: JsonValueCodec, Resp: JsonValueCodec](
      version: String,
      responseBuilder: ResponsePayload => Resp): AuthedService[User, F] = AuthedService {
    case authed @ POST -> Root / `version` / "webhook" as _ =>
      for {
        dfRequest <- authed.req.as[Req]
        payload   <- service.processRequest(dfRequest).value
        resp      <- payload.fold(H.handle, data => Ok(responseBuilder(data)))
      } yield resp
  }

  override def endpoints: AuthedService[User, F] =
    versioned[Request1, Response1](
      "v1",
      payload => Response1("", payload, "posters")
    ) <+> versioned[Request2, Response2](
      "v2",
      payload => Response2("", payload, "posters")
    )
}

object DfWebhookEndpoint {
  def apply[F[_]: Effect: HttpErrorHandler](service: DfWebhookService[F]): DfWebhookEndpoint[F] =
    new DfWebhookEndpoint(service)
}