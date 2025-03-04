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

import javax.inject.{Inject, Singleton}
import play.api.data.{Form, Forms}
import play.api.mvc.*
import uk.gov.hmrc.platformstatusfrontend.services.MemoryHog
import uk.gov.hmrc.platformstatusfrontend.views.html.Kill

import scala.concurrent.Future
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

case class LeakRequest(
  mb   : Int = 10,
  sleep: Int = 100
)

@Singleton
class KillController @Inject()(
  mcc      : MessagesControllerComponents,
  memoryHog: MemoryHog,
  killView : Kill
) extends FrontendController(mcc):

  val leakForm: Form[LeakRequest] =
    Form(
      Forms.mapping(
        "mb"    -> Forms.number,
        "sleep" -> Forms.number
      )(LeakRequest.apply)(o => Some(Tuple.fromProductTyped(o)))
    )

  def kill: Action[AnyContent] =
    Action.async: request =>
      given MessagesRequestHeader = request
      Future.successful(Ok(killView(leakForm.fill(LeakRequest()))))

  def meteOutDeath: Action[AnyContent] =
    Action:
      System.exit(0)
      Redirect(routes.KillController.kill)
        .flashing("success" -> "If you see this then the container did not die.")

  def leakMemory: Action[AnyContent] =
    Action: request =>
      given MessagesRequest[AnyContent] = request
      leakForm.bindFromRequest()
        .fold(
          formWithErrors => BadRequest(killView(formWithErrors)),
          killRequest =>
            memoryHog.eatMemory(killRequest.mb, killRequest.sleep)
            Redirect(routes.KillController.kill)
              .flashing("success" -> "If you see this then the container did not die.")
        )
