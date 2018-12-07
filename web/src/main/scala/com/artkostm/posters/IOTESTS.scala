package com.artkostm.posters

import java.util.concurrent.Executors

import cats.effect.IO
import scala.concurrent._

object IOTESTS extends App {
  val Main           = ExecutionContext.global
  val BlockingFileIO = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())
  val test = for {
    _   <- IO(println("first: " + Thread.currentThread().getName))
    lol <- IO.shift(BlockingFileIO).flatMap(_ => IO.shift(Main)).flatMap(_ => as)
    _ <- {
      IO(println("second " + lol + s": ${Thread.currentThread().getName}"))
    }
  } yield ()

  test.unsafeRunAsync {
    case Right(a) => println(a)
    case Left(e)  => e.printStackTrace()
  }

  Thread.sleep(3000)
  def as: IO[String] = IO.async[String] { cb =>
    println("async: " + Thread.currentThread().getName)
    cb(Right("lol"))
//    cb(Left(new RuntimeException))
  }
}
