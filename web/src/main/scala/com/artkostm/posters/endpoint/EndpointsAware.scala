package com.artkostm.posters.endpoint

import com.artkostm.posters.interfaces.auth.User
import org.http4s.AuthedRoutes

trait EndpointsAware[F[_]] {
  def endpoints: AuthedRoutes[User, F]
}
