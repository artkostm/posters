package com.artkostm.posters.jsoniter

import cats.Applicative
import com.github.plokhotnyuk.jsoniter_scala.core._
import fs2.Chunk
import org.http4s.{EntityEncoder, MediaType}
import org.http4s.headers.`Content-Type`

trait JsoniterInstances {
  implicit def jsoniterEntityEncoder[F[_]: Applicative, A](implicit codec: JsonValueCodec[A]): EntityEncoder[F, A] =
    EntityEncoder[F, Chunk[Byte]].contramap[A] { entity =>
      val bytes = writeToArray(entity)(codec)
      Chunk.bytes(bytes)
    }
    .withContentType(`Content-Type`(MediaType.`application/json`))
}
