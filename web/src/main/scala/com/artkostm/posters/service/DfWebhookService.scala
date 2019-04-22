package com.artkostm.posters.service

import cats.implicits._
import cats.Monad
import cats.data.{EitherT => ET, NonEmptyList}
import com.artkostm.posters.ValidationError.DfWebhookError
import com.artkostm.posters.algebra.EventStore
import com.artkostm.posters.categories.Category
import com.artkostm.posters.interfaces.dialog.ResponsePayload
import com.artkostm.posters.service.DfKeyData._

class DfWebhookService[F[_]: Monad](repository: EventStore[F]) {

  def processRequest[DfRequest: DfKeyData](request: DfRequest): ET[F, DfWebhookError, ResponsePayload] =
    if (request.incomplete) {
      (request.date, request.period) match {
        case (Some(date), None) =>
          if (request.hasAllCategory) {
            // TODO: fix all category extracting
            ET.liftF(
              repository
                .findByNamesAndDate(NonEmptyList.of(Category.All.entryName), date)
                .map(ResponsePayload(_)))
          } else {
            ET.liftF(
              repository
                .findByNamesAndDate(NonEmptyList.fromListUnsafe(request.categories), date)
                .map(ResponsePayload(_)))
          }
        case (None, Some(period)) =>
          if (request.hasAllCategory) {
            // TODO: fix all category extracting
            ET.liftF(
              repository
                .findByNamesAndPeriod(NonEmptyList.of(Category.All.entryName), period.startDate, period.endDate)
                .map(ResponsePayload(_)))
          } else {
            ET.liftF(
              repository
                .findByNamesAndPeriod(NonEmptyList.fromListUnsafe(request.categories), period.startDate, period.endDate)
                .map(ResponsePayload(_)))
          }
        case _ => ET.leftT(DfWebhookError("Cannot extract date or period!"))
      }
    } else {
      ET.leftT(DfWebhookError("Action is incomplete!"))
    }

}
