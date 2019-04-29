package com.artkostm.posters.endpoint

import java.time.LocalDate

import com.artkostm.posters.categories
import com.artkostm.posters.endpoint.auth.role.Role
import com.artkostm.posters.interfaces.auth.User
import com.artkostm.posters.interfaces.dialog.v1.{ContextParams, Datetime, Parameters, Period}
import com.artkostm.posters.interfaces.event.{Comment, EventData, EventInfo}
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary.arbitrary

trait PostersArbitraries {
  case class Link(value: String)

  implicit val link = Arbitrary[Link] {
    for {
      link <- Gen.alphaStr
    } yield Link(link)
  }

  implicit val comment = Arbitrary[Comment] {
    for {
      author <- Gen.alphaStr
      date   <- Gen.alphaNumStr
      text   <- Gen.asciiPrintableStr
      rating <- Gen.option(Gen.alphaStr)
    } yield Comment(author, date, text, rating)
  }

  implicit val eventData = Arbitrary[EventData] {
    for {
      description <- Gen.asciiPrintableStr
      numPhotos   <- Gen.choose(1, 10)
      photos      <- Gen.listOfN(numPhotos, Gen.alphaStr).map(_.map(x => s"https://$x.jpg"))
      numComments <- Gen.choose(1, 10)
      comments    <- Gen.listOfN(numComments, arbitrary[Comment])
    } yield EventData(description, photos, comments)
  }

  implicit val eventInfo = Arbitrary[EventInfo] {
    for {
      link <- Gen.alphaStr
      data <- arbitrary[EventData]
    } yield EventInfo(s"https://$link/", data)
  }

  implicit val user = Arbitrary[User] {
    for {
      apiKey <- Gen.alphaNumStr
      role   <- Gen.oneOf(Role.User.entryName, Role.Volunteer.entryName)
      id     <- Gen.alphaStr
    } yield User(apiKey, role, id)
  }
}

trait DialogflowV1Arbitraries { this: BaseArbitraries =>

  implicit val period1 = Arbitrary[Period] {
    for {
      start   <- arbitrary[LocalDate](this.localDate)
      numDays <- Gen.choose(1, 20)
    } yield Period(start.plusDays(numDays), start)
  }

  implicit val datetime1 = Arbitrary[Datetime] {
    for {
      date   <- Gen.option(arbitrary[LocalDate](this.localDate))
      p      <- Gen.option(arbitrary[Period])
      period = if (date.isDefined) None else p
    } yield Datetime(date, period)
  }

  implicit val parameters = Arbitrary[Parameters] {
    for {
      numCategories <- Gen.choose(0, 8)
      category      <- Gen.containerOfN[List, categories.Category](numCategories, Gen.oneOf(categories.Category.values))
      datetime      <- arbitrary[Datetime]
    } yield Parameters(category.map(_.entryName), datetime)
  }

  implicit val contextParams = Arbitrary[ContextParams] {
    for {
      datetime         <- Gen.alphaNumStr
      datetimeOriginal <- Gen.alphaNumStr
      numCategories    <- Gen.choose(0, 8)
      category         <- Gen.containerOfN[List, categories.Category](numCategories, Gen.oneOf(categories.Category.values))
    } yield
      ContextParams(datetime, datetimeOriginal, category.map(_.entryName), category.map(_.entryName).mkString(","))
  }
}

trait BaseArbitraries {
  implicit val localDate = Arbitrary[LocalDate] {
    for {
      millis <- Gen.posNum[Long]
    } yield LocalDate.ofEpochDay(millis)
  }
}

object PostersArbitraries extends PostersArbitraries
