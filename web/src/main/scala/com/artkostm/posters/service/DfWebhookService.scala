package com.artkostm.posters.service

import cats.implicits._
import cats.Monad
import cats.data.{EitherT => ET, NonEmptyList}
import com.artkostm.posters.ValidationError.DfWebhookError
import com.artkostm.posters.algebra.EventStore
import com.artkostm.posters.interfaces.dialog.ResponsePayload
import com.artkostm.posters.service.DfKeyData._

/**
  * Service to process webhook requests from Dialogflow
  * @param repository - a thing that knows how and where to find event data
  * @param `monad$F`
  * @tparam F - effect
  */
class DfWebhookService[F[_]: Monad](repository: EventStore[F]) {

  def processRequest[DfRequest: DfKeyData](request: DfRequest): ET[F, DfWebhookError, ResponsePayload] =
    if (!request.incomplete) {
      (request.date, request.period) match {
        case (Some(date), _) =>
          if (request.hasAllCategory) {
            ET.fromOptionF(repository.findByDate(date).map(o => o.map(day => ResponsePayload(day.categories))),
                           DfWebhookError(s"Cannot find event day using date=$date"))
          } else {
            fromCategoryList(request.categories) { categories =>
              repository.findByNamesAndDate(categories, date).map(ResponsePayload(_))
            }
          }
        case (_, Some(period)) =>
          if (request.hasAllCategory) {
            ET.liftF(
              repository
                .findByPeriod(period.startDate, period.endDate)
                .map(days => ResponsePayload(days.flatMap(_.categories))))
          } else {
            fromCategoryList(request.categories) { categories =>
              repository.findByNamesAndPeriod(categories, period.startDate, period.endDate).map(ResponsePayload(_))
            }
          }
        case _ => ET.leftT(DfWebhookError("Cannot extract date or period!"))
      }
    } else {
      ET.leftT(DfWebhookError("Action is incomplete!"))
    }

  private def fromCategoryList(categories: List[String])(
      payloadf: NonEmptyList[String] => F[ResponsePayload]): ET[F, DfWebhookError, ResponsePayload] =
    ET.fromOption[F](NonEmptyList.fromList(categories), DfWebhookError("List of categories cannot be empty!"))
      .flatMap(categories => ET.liftF(payloadf(categories)))
}
