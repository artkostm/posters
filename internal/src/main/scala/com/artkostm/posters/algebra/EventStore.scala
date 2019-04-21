package com.artkostm.posters.algebra
import java.time.LocalDate

import cats.data.NonEmptyList
import com.artkostm.posters.interfaces.schedule.{Category, Day}

trait EventStore[F[_]] {
  def deleteOld(today: LocalDate): F[Int]
  def save(day: Day): F[Day]
  def findByDate(day: LocalDate): F[Option[Day]]
  def findByNames(names: NonEmptyList[String]): F[List[Category]]
  def findByNamesAndDate(name: NonEmptyList[String], date: LocalDate): F[Option[Category]]
  def findByNamesAndPeriod(names: NonEmptyList[String], start: LocalDate, end: LocalDate): F[List[Category]]
}
