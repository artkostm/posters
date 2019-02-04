package com.github.artkostm.server
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
import cats.data.EitherT
import cats.implicits._
import cats.effect.IO
import cats.effect.Effect
import org.http4s.{Status => _, _}
import org.http4s.circe._
import org.http4s.client.{Client => Http4sClient}
import org.http4s.client.blaze._
import org.http4s.client.UnexpectedStatus
import org.http4s.dsl.io.Path
import org.http4s.multipart._
import org.http4s.headers._
import org.http4s.EntityEncoder._
import org.http4s.EntityDecoder._
import fs2.Stream
import io.circe.Json
import scala.language.higherKinds
import scala.language.implicitConversions
import cats.implicits._
import cats.data.EitherT
import scala.concurrent.Future
import com.github.artkostm.server.Implicits._
object Http4sImplicits {
  import scala.util.Try
  private[this] def pathEscape(s: String): String                   = Path(s, "").toString.init.tail
  implicit def addShowablePath[T](implicit ev: Show[T]): AddPath[T] = AddPath.build[T](v => pathEscape(ev.show(v)))
  private[this] def argEscape(k: String, v: String): String         = Query.apply((k, Some(v))).toString
  implicit def addShowableArg[T](implicit ev: Show[T]): AddArg[T] =
    AddArg.build[T](key => v => argEscape(key, ev.show(v)))
  type TraceBuilder[F[_]] = String => org.http4s.client.Client[F] => org.http4s.client.Client[F]
  implicit def emptyEntityEncoder[F[_]: Effect]: EntityEncoder[F, EntityBody[Nothing]] = EntityEncoder.emptyEncoder
  object DoubleNumber     { def unapply(value: String): Option[Double]     = Try(value.toDouble).toOption    }
  object BigDecimalNumber { def unapply(value: String): Option[BigDecimal] = Try(BigDecimal(value)).toOption }
  object BigIntNumber     { def unapply(value: String): Option[BigInt]     = Try(BigInt(value)).toOption     }
}
