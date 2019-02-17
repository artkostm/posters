package com.artkostm.posters.endpoint

import com.artkostm.posters.interfaces.auth.User
import org.http4s.AuthedService

trait EndpointsAware[F[_]] {
  def endpoints: AuthedService[User, F]
}
