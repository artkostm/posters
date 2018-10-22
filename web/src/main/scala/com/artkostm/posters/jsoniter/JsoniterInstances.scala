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
    EntityEncoder.byteArrayEncoder[F]
      .contramap[A](writeToArray(_))
      .withContentType(`Content-Type`(MediaType.`application/json`))

  implicit def jsoniterEntityDecoder[F[_]: Sync, A](implicit codec: JsonValueCodec[A]): EntityDecoder[F, A] =
    EntityDecoder.decodeBy(MediaType.`application/json`) { msg =>
      EntityDecoder.collectBinary(msg).flatMap { segment =>
        val bb = ByteBuffer.wrap(segment.force.toArray)
        if (bb.hasRemaining) {
          Try(readFromArray(bb.array())).toEither match {
            case Right(json) =>
              DecodeResult.success[F, A](json)
            case Left(pf) =>
              DecodeResult.failure[F, A](
                MalformedMessageBodyFailure("Invalid JSON", Some(pf)))
          }
        } else {
          DecodeResult.failure[F, A](MalformedMessageBodyFailure("Invalid JSON: empty body", None))
        }
      }
    }

  //implicit def mapValueCodec[A: JsonValueCodec]: JsonValueCodec[Map[String, A]] = new MapValueCodec[A]
}

class SeqOfTuplesValueCodec[A : JsonValueCodec] extends JsonValueCodec[Seq[A]] {
  override def decodeValue(in: JsonReader, default: Seq[A]): Seq[A] =
    if (in.isNextToken('['))
      if (in.isNextToken(']'))
        default
      else
      {
        in.rollbackToken()
        val x = new scala.collection.mutable.ListBuffer[A]()
        do
          x.+=(implicitly[JsonValueCodec[A]].decodeValue(in, null.asInstanceOf[A]))
        while (in.isNextToken(','))
        if (in.isCurrentToken(']'))
          x
        else
          in.arrayEndOrCommaError()
      }
    else
      in.readNullOrTokenError(default, '[')

  override def encodeValue(x: Seq[A], out: JsonWriter): Unit = x match {
    case null => out.writeNull()
    case _ =>
      out.writeArrayStart()
//      x.reduce {(first, second) =>
//        implicitly[JsonValueCodec[A]].encodeValue(first, out)
//        out.writeComma()
//        implicitly[JsonValueCodec[A]].encodeValue(second, out)
//      }
      out.writeArrayEnd()
  }

  override def nullValue: Seq[A] = Seq.empty
}

class MapValueCodec[A : JsonValueCodec] extends JsonValueCodec[Map[String, A]] {
  override def decodeValue(in: JsonReader, default: Map[String, A]): Map[String, A] =
    if (in.isNextToken('{'))
      if (in.isNextToken('}'))
        default
      else
      {
        in.rollbackToken()
        var x = nullValue
        do
          x = x.updated(in.readKeyAsString(), implicitly[JsonValueCodec[A]].decodeValue(in, null.asInstanceOf[A]))
        while (in.isNextToken(','))
        if (in.isCurrentToken('}'))
          x
        else
          in.objectEndOrCommaError()
      }
    else
      in.readNullOrTokenError(default, '{')

  override def encodeValue(x: Map[String, A], out: JsonWriter): Unit = x match {
    case null => out.writeNull()
    case _ =>
      out.writeObjectStart()
      x.foreach(pair => {
        out.writeKey(pair._1)
        implicitly[JsonValueCodec[A]].encodeValue(pair._2, out)
      })
      out.writeObjectEnd()
  }

  override def nullValue: Map[String, A] = Map.empty
}