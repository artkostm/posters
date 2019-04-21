package com.artkostm.posters.service

import cats.data.NonEmptyList
import com.artkostm.posters.algebra.EventStore
import com.artkostm.posters.service.DfKeyData._

class DfWebhookService[F[_]](repository: EventStore[F]) {

  def execute[DfRequest: DfKeyData](request: DfRequest) = {
    if (request.incomplete) {
      request.date match {
        case Some(date) =>
          if (request.hasAllCategory) {
            repository.findByDate(date)
          } else {
            repository.findByNamesAndDate(NonEmptyList.fromListUnsafe(request.categories), date)
          }
      }
    }
  }

}
