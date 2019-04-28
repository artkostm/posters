package com.artkostm.posters.endpoint

import java.time.LocalDate

import com.artkostm.posters.endpoint.auth.role.Role
import com.artkostm.posters.interfaces.auth.User
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

  implicit val localDate = Arbitrary[LocalDate] {
    for {
      millis <- Gen.posNum[Long]
    } yield LocalDate.ofEpochDay(millis)
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

object PostersArbitraries extends PostersArbitraries
