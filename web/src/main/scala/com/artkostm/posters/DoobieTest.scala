package com.artkostm.posters

import doobie._
import doobie.implicits._
//import cats._
import cats.effect._
import cats.implicits._

object DoobieTest extends App {
  case class User(name: String, age: Int)

  val program = User("Artsiom", 24).pure[ConnectionIO]

//  import cats.~>
//  import cats.data.Kleisli
//  import doobie.free.connection.ConnectionOp
//  import java.sql.Connection

  val interpreter = KleisliInterpreter[IO].ConnectionInterpreter
  val kleisli = program.foldMap(interpreter)
  val io = IO(null: java.sql.Connection) >>= kleisli.run

  println(io.unsafeRunSync)
}
