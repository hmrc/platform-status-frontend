/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.platformstatusfrontend.controllers

import cats.implicits._
import play.api.Logger
import play.api.mvc._
import uk.gov.hmrc.platformstatusfrontend.services.StatusChecker
import uk.gov.hmrc.platformstatusfrontend.views.html.Status
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class StatusController @Inject()(
  mcc              : MessagesControllerComponents,
  val statusChecker: StatusChecker,
  statusView       : Status
)(using
  ec: ExecutionContext
) extends FrontendController(mcc):

  def defaultLanding: Action[AnyContent] =
    Action:
      Redirect(routes.StatusController.platformStatus)

  def platformStatus: Action[AnyContent] =
    Action.async:
      implicit request =>
        Logger("StatusController").info(s"in platformStatus: ${org.slf4j.MDC.getCopyOfContextMap}")
        (
          statusChecker.iteration1Status(),
          statusChecker.iteration2Status(),
          statusChecker.iteration3Status(),
          statusChecker.iteration4Status(),
          statusChecker.iteration5Status()
        ).mapN((iter1, iter2, iter3, iter4, iter5) =>
          Ok(statusView(List(
            iter1,
            iter2,
            iter3,
            iter4,
            iter5
          )))
        )
