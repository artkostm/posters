package com.artkostm.posters.controllers.auth

import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future
import javax.inject.{Inject, Singleton}

@Singleton class AuthFilter extends SimpleFilter[Request, Response]{
  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = service(request)
}
