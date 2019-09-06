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
import uk.gov.hmrc.platformstatusfrontend.services.StatusChecker
import uk.gov.hmrc.platformstatusfrontend.views
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

case class NoiseRequest(level: String = "INFO", message: String = "###platform-status-frontend###", amount: Int = 1)


@Singleton
class NoiseController @Inject()(appConfig: AppConfig, mcc: MessagesControllerComponents, val statusChecker: StatusChecker)
  extends FrontendController(mcc) {

  implicit val config: AppConfig = appConfig
  val logger: Logger = Logger(this.getClass)

  val noiseForm: Form[NoiseRequest] = Form(
    mapping(
      "level" -> text,
      "message" -> text,
      "amount"  -> number
    )(NoiseRequest.apply)(NoiseRequest.unapply)
  )

  def noise = Action.async { implicit request =>
    Future.successful(Ok(views.html.noise(noiseForm.fill(NoiseRequest()))))
  }

  def createNoise = Action { implicit request =>
    noiseForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.noise(formWithErrors))
      },
      noiseRequest => {
        makeSomeNoise(noiseRequest)
        Redirect(routes.NoiseController.noise()).flashing("success" -> "Log messages written.")
      }
    )
  }

  private def makeSomeNoise(request: NoiseRequest) = {
    for(i <- 1 to request.amount) {
      request.level match {
        case "ERROR" => logger.error(s"$i: " + request.message, new RuntimeException(request.message))
        case "WARN" => logger.warn(s"$i: " + request.message)
        case "INFO" => logger.info(s"$i: " + request.message)
        case _ => logger.warn("Unrecognized log level")
      }
    }
  }

}


