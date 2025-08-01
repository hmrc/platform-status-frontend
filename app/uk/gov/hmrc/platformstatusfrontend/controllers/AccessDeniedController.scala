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

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, MessagesRequestHeader}
import uk.gov.hmrc.platformstatusfrontend.views.html.ErrorTemplate
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.Future

class AccessDeniedController @Inject() (
  mcc : MessagesControllerComponents,
  view: ErrorTemplate
) extends FrontendController(mcc):

  private val heading = "You do not have permission to access this service"
  private val message = "Contact Platops if you think you do have permission"

  def deny: Action[AnyContent] =
    Action.async: request =>
      given MessagesRequestHeader = request
      Future.successful(Forbidden(view(heading,heading,message)))
