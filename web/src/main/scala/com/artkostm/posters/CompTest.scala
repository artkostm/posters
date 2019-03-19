package com.artkostm.posters

import qq.droste._
import qq.droste.data._
import cats.implicits._

object CompTest extends App {
  val natCoalgebra: Coalgebra[Option, BigDecimal] =
    Coalgebra(n => if (n > 0) Some(n - 1) else None)

  val fibAlgebra: CVAlgebra[Option, BigDecimal] = CVAlgebra {
    case Some(r1 :< Some(r2 :< _)) => r1 + r2
    case Some(_ :< None)           => 1
    case None                      => 0
  }

  val fib: BigDecimal => BigDecimal = scheme.ghylo(
    fibAlgebra.gather(Gather.histo),
    natCoalgebra.scatter(Scatter.ana))

  println(fib(1366))

  for (_ <- 10000) {

  }
}
