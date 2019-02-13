package com.artkostm.posters.endpoint.auth
import cats.effect.Sync
import tsec.jws.mac.JWTMac
import tsec.jwt.JWTClaims
import tsec.mac.jca.{HMACSHA256, MacSigningKey}

class TokenGenerator[F[_]](implicit F: Sync[F]) {
  private def generateJwtKey(token: String): F[MacSigningKey[HMACSHA256]] = {
    F.catchNonFatal(HMACSHA256.unsafeBuildKey(token.getBytes))
  }

  private def generateToken(claims: JWTClaims, jwtKey: MacSigningKey[HMACSHA256]): F[String] =
    JWTMac.buildToString(claims, jwtKey)

  private val ifEmpty: F[String] = F.raiseError(new Exception("Api Token not found"))

  val tokenGenerator: F[String] = ApiToken.fold(ifEmpty) { apiToken =>
    for {
      jwtKey  <- generateJwtKey(apiToken)
      claims  = JWTClaims(subject = ApiKey, expiration = None)
      token   <- generateToken(claims, jwtKey)
    } yield token
  }
}
