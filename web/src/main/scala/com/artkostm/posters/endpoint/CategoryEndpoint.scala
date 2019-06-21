package com.artkostm.posters.endpoint

import cats.data.{EitherT, NonEmptyList}
import cats.implicits._
import cats.effect.Sync
import com.artkostm.posters.ValidationError.{CategoriesNotFoundError, CategoryNotFoundError}
import com.artkostm.posters.algebra.EventStore
import com.artkostm.posters.categories.CategoryVar
import com.artkostm.posters.endpoint.error.HttpErrorHandler
import com.artkostm.posters.jsoniter._
import com.artkostm.posters.jsoniter.codecs._
import com.artkostm.posters.interfaces.auth.User
import com.artkostm.posters.scraper.Scraper
import org.http4s.AuthedRoutes
import org.http4s.dsl.Http4sDsl

class CategoryEndpoint[F[_]: Sync](repository: EventStore[F], scraper: Scraper[F])(implicit H: HttpErrorHandler[F])
    extends Http4sDsl[F]
    with EndpointsAware[F] {

  private def getCategoryByName(): AuthedRoutes[User, F] = AuthedRoutes.of {
    case GET -> Root / ApiVersion / "categories" / CategoryVar(categoryName) :? DateMatcher(date) as _ =>
      for {
        category <- EitherT
                     .fromOptionF(
                       repository.findByNamesAndDate(NonEmptyList.of(categoryName.entryName), date).map(_.headOption),
                       CategoryNotFoundError(categoryName.entryName, date))
                     .value
        resp <- category.fold(H.handle, Ok(_))
      } yield resp
  }

  private def getDay(): AuthedRoutes[User, F] = AuthedRoutes.of {
    case GET -> Root / ApiVersion / "categories" :? DateMatcher(date) as _ =>
      for {
        category <- EitherT
                     .fromOptionF(repository.findByDate(date), CategoriesNotFoundError(date))
                     .value
        resp <- category.fold(H.handle, Ok(_))
      } yield resp
  }

  override def endpoints: AuthedRoutes[User, F] = getCategoryByName() <+> getDay()
}

object CategoryEndpoint {
  def apply[F[_]: Sync: HttpErrorHandler](repository: EventStore[F], scraper: Scraper[F]): CategoryEndpoint[F] =
    new CategoryEndpoint(repository, scraper)
}
