package com.artkostm.posters.graphql.midleware

import sangria.execution._
import sangria.schema.Context

object TokenEnforcer extends Middleware[SecurityContext] with MiddlewareBeforeField[SecurityContext] {
  override type QueryVal = Unit
  override type FieldVal = Unit

  override def beforeQuery(context: MiddlewareQueryContext[SecurityContext, _, _]): Unit = ()

  override def afterQuery(queryVal: Unit, context: MiddlewareQueryContext[SecurityContext, _, _]): Unit = ()

  override def beforeField(queryVal: Unit,
                           mctx: MiddlewareQueryContext[SecurityContext, _, _],
                           ctx: Context[SecurityContext, _]): BeforeFieldResult[SecurityContext, Unit] = {
    val permisions = ctx.field.tags.collect { case Permission(name) => name }
    continue
  }
}

case object Authorized extends FieldTag
case class Permission(name: String) extends FieldTag
