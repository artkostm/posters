package com.artkostm.posters.jsoniter

import cats._
import cats.effect._
import java.nio.ByteBuffer
import com.github.plokhotnyuk.jsoniter_scala.core._
import org.http4s.{DecodeResult, EntityDecoder, EntityEncoder, MalformedMessageBodyFailure, MediaType}
import org.http4s.headers.`Content-Type`

import scala.util.Try

trait JsoniterInstances {
  implicit def jsoniterEntityEncoder[F[_]: Applicative, A: JsonValueCodec]: EntityEncoder[F, A] =
    EntityEncoder
      .byteArrayEncoder[F]
      .contramap[A](writeToArray(_))
      .withContentType(`Content-Type`(MediaType.`application/json`))

  implicit def jsoniterEntityDecoder[F[_]: Sync, A: JsonValueCodec]: EntityDecoder[F, A] =
    EntityDecoder.decodeBy(MediaType.`application/json`) { msg =>
      EntityDecoder.collectBinary(msg).flatMap { segment =>
        val bb = ByteBuffer.wrap(segment.force.toArray)
        if (bb.hasRemaining) {
          Try(readFromArray(bb.array())).toEither match {
            case Right(json) =>
              DecodeResult.success[F, A](json)
            case Left(pf) =>
              DecodeResult.failure[F, A](MalformedMessageBodyFailure("Invalid JSON", Some(pf)))
          }
        } else {
          DecodeResult.failure[F, A](MalformedMessageBodyFailure("Invalid JSON: empty body", None))
        }
      }
    }
}
