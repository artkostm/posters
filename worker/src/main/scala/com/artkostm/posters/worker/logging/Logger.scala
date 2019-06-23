package com.artkostm.posters.worker.logging

import cats.effect.Sync

class Logger[F[_]: Sync] {
  def error(message: => String): F[Unit] = Sync[F].delay(println("error"))
  def error(t: Throwable)(message: => String): F[Unit] = Sync[F].delay(println("error"))
  def warn (message: => String): F[Unit] = Sync[F].delay(println("warn"))
  def info (message: => String): F[Unit] = Sync[F].delay(println("info"))
  def debug(message: => String): F[Unit] = Sync[F].delay(println("debug"))
  def trace(message: => String): F[Unit] = Sync[F].delay(println("trace"))
}
