package com.artkostm.posters.endpoint.auth

import cats.effect.{ExitCode, IO, IOApp, Sync}
import tsec.mac.jca.{HMACSHA256, MacSigningKey}
import cats.syntax.flatMap._
import cats.syntax.functor._
import io.circe.Json
import tsec.jws.mac.JWSMacCV.genSigner
import tsec.jws.mac.JWTMac
import tsec.jwt.JWTClaims

object ApiTokenGenerator extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    for {
      _     <- IO(println("Generating API token"))
      token <- new TokenGenerator[IO]().tokenGenerator
      _     <- IO(println(token))
    } yield ExitCode.Success
}

class TokenGenerator[F[_]](implicit F: Sync[F]) {

  private val ApiToken = sys.env.get("P_API_TOKEN")
  private val ApiKey   = sys.env.get("P_API_KEY")

  private def generateJwtKey(token: String): F[MacSigningKey[HMACSHA256]] =
    F.catchNonFatal(HMACSHA256.unsafeBuildKey(token.getBytes))

  private def generateToken(claims: JWTClaims, jwtKey: MacSigningKey[HMACSHA256]): F[String] =
    JWTMac.buildToString(claims, jwtKey)

  private val ifEmpty: F[String] = F.raiseError(new Exception("Api Token not found"))

  val tokenGenerator: F[String] = ApiKey.fold(ifEmpty) { apiKey =>
    for {
      jwtKey <- HMACSHA256.buildKey[F](apiKey.getBytes)
      claims = JWTClaims(issuer = Some("2"),
                         subject = Some("User"),
                         expiration = None,
                         customFields = Seq(("atk", Json.fromString(ApiToken.getOrElse("")))))
      token <- generateToken(claims, jwtKey)
    } yield token
  }
}
