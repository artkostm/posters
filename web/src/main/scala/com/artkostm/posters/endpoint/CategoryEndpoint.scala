package com.artkostm.posters.endpoint

import cats.data.EitherT
import cats.implicits._
import cats.effect.Effect
import com.artkostm.posters.algebra.EventStore
import com.artkostm.posters.categories.CategoryVar
import com.artkostm.posters.interfaces.auth.User
import com.artkostm.posters.scraper.Scraper
import org.http4s.AuthedService
import org.http4s.dsl.Http4sDsl

class CategoryEndpoint[F[_]: Effect](repository: EventStore[F], scraper: Scraper[F]) extends Http4sDsl[F] with EndpointsAware[F] {
  import com.artkostm.posters.jsoniter._
  import ValueCodecs._
  import com.artkostm.posters.ValidationError._

  private def getCategoryByName(): AuthedService[User, F] = AuthedService {
    case GET -> Root / "categories" / CategoryVar(categoryName) :? DateMatcher(date) as _ =>
      for {
        category <- EitherT
                     .fromOptionF(repository.findByNameAndDate(categoryName.entryName, date),
                                  CategoryNotFound(s"Cannot find '$categoryName' category using date=$date"))
                     .value
        resp <- category.fold(NotFound(_), Ok(_))
      } yield resp
  }

  override def endpoints: AuthedService[User, F] = getCategoryByName()
}

object CategoryEndpoint {
  def apply[F[_]: Effect](repository: EventStore[F], scraper: Scraper[F]): AuthedService[User, F] =
    new CategoryEndpoint(repository, scraper).endpoints
}
