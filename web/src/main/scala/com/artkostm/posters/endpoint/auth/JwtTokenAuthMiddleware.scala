package com.artkostm.posters.endpoint.auth

import cats.data.{EitherT, Kleisli, OptionT}
import cats.effect.Sync
import cats.syntax.applicativeError._
import cats.syntax.functor._
import com.artkostm.posters.config.ApiConfig
import com.artkostm.posters.endpoint.ApiError
import com.artkostm.posters.endpoint.auth.JwtTokenAuthMiddleware.AuthConfig
import com.artkostm.posters.endpoint.auth.role.Role
import com.artkostm.posters.interfaces.auth.User
import org.http4s.Credentials.Token
import org.http4s.{AuthScheme, AuthedRoutes, Request}
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Authorization
import org.http4s.server.AuthMiddleware
import tsec.jws.mac.JWTMac
import tsec.mac.jca.{HMACSHA256, MacSigningKey, MacVerificationError}
import eu.timepit.refined.auto._

object JwtTokenAuthMiddleware {
  def apply[F[_]: Sync](api: ApiConfig): F[AuthMiddleware[F, User]] =
    new Middleware[F](api).middleware

  case class AuthConfig(jwtKey: MacSigningKey[HMACSHA256])
}

class Middleware[F[_]](api: ApiConfig)(implicit F: Sync[F]) {

  private val ifEmpty = F.raiseError[AuthMiddleware[F, User]](new Exception("Api Token not found"))

  private def generateJwtKey(token: String): F[MacSigningKey[HMACSHA256]] =
   F.catchNonFatal(HMACSHA256.unsafeBuildKey(token.getBytes))
//    HMACSHA256.buildKey(token.getBytes)

  val middleware: F[AuthMiddleware[F, User]] = Option(api.token).fold(ifEmpty) { token =>
    generateJwtKey(token.value).map { jwtKey =>
      val config = AuthConfig(jwtKey)
      new JwtTokenAuthMiddleware[F](config, api.key.value).middleware
    }
  }
}

class JwtTokenAuthMiddleware[F[_]: Sync](config: AuthConfig, apiKey: String) extends Http4sDsl[F] {
  import com.artkostm.posters.jsoniter._

  private val onFailure: AuthedRoutes[String, F] =
    Kleisli(_ => OptionT.liftF(Forbidden(ApiError("Forbidden! You don't have enough permissions!", 403))))

  private def bearerTokenFromRequest(request: Request[F]): OptionT[F, String] =
    OptionT.fromOption[F] {
      request.headers.get(Authorization).collect {
        case Authorization(Token(AuthScheme.Bearer, token)) => token
      }
    }

  private def verifyToken(request: Request[F], jwtKey: MacSigningKey[HMACSHA256]): OptionT[F, User] =
    for {
      token    <- bearerTokenFromRequest(request)
      verified <- OptionT.liftF(JWTMac.verifyAndParse[F, HMACSHA256](token, jwtKey))
      accessToken <- OptionT.fromOption[F] {
                      for {
                        apiKey <- verified.body.subject
                        role   <- verified.body.issuer
                        id     <- verified.body.getCustom[String]("id").toOption
                      } yield User(apiKey, role, id)
                    }
    } yield accessToken

  private def authUser(jwtKey: MacSigningKey[HMACSHA256]): Kleisli[F, Request[F], Either[String, User]] =
    Kleisli { request =>
      verifyToken(request, jwtKey).value
        .map { option =>
          Either.cond[String, User](option.exists(u => u.apiKey == apiKey && Role.exists(u.role)),
                                    option.get,
                                    "Unable to authorize token")
        }
        .recoverWith {
          case MacVerificationError(msg) => EitherT.leftT(msg).value
        }
    }

  def middleware: AuthMiddleware[F, User] =
    AuthMiddleware(authUser(config.jwtKey), onFailure)
}
