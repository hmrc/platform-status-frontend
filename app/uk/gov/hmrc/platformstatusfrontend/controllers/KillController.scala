/*
 * Copyright 2019 HM Revenue & Customs
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
import play.api.Logger
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import uk.gov.hmrc.platformstatusfrontend.config.AppConfig
import uk.gov.hmrc.platformstatusfrontend.services.{MemoryHog, StatusChecker}
import uk.gov.hmrc.platformstatusfrontend.views
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

case class LeakRequest(mb: Int = 10, sleep: Int = 100)

@Singleton
class KillController @Inject()(appConfig: AppConfig, mcc: MessagesControllerComponents, memoryHog: MemoryHog) extends FrontendController(mcc) {

  val leakForm: Form[LeakRequest] = Form(
    mapping(
      "mb" -> number,
      "sleep"  -> number
    )(LeakRequest.apply)(LeakRequest.unapply)
  )

  implicit val config: AppConfig = appConfig
  val logger: Logger = Logger(this.getClass)

  def kill = Action.async { implicit request =>
    Future.successful( Ok(views.html.kill( leakForm.fill(LeakRequest()))  ) )
  }

  def meteOutDeath = Action { implicit request =>
    System.exit(0)
    Redirect(routes.KillController.kill()).flashing("success" -> "If you see this then the container did not die.")
  }

  def leakMemory = Action { implicit request =>
    leakForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.kill(formWithErrors))
      },
      killRequest => {
        memoryHog.eatMemory(killRequest.mb, killRequest.sleep)
        Redirect(routes.KillController.kill()).flashing("success" -> "If you see this then the container did not die.")
      }
    )
  }

}


