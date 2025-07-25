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
import play.api.Logger
import play.api.data.{Form, Forms}
import play.api.mvc.*
import uk.gov.hmrc.platformstatusfrontend.views.html.Noise
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

case class NoiseRequest(
  level  : String = "INFO",
  message: String = "###platform-status-frontend###",
  amount : Int    = 1
)


@Singleton
class NoiseController @Inject()(
  mcc          : MessagesControllerComponents,
  noiseView    : Noise
) extends FrontendController(mcc):

  private val logger: Logger = Logger(this.getClass)

  val noiseForm: Form[NoiseRequest] =
    Form(
      Forms.mapping(
        "level"   -> Forms.text,
        "message" -> Forms.text,
        "amount"  -> Forms.number
      )(NoiseRequest.apply)(o => Some(Tuple.fromProductTyped(o)))
    )

  def noise: Action[AnyContent] =
    Action: request =>
      given MessagesRequestHeader = request
      Ok(noiseView(noiseForm.fill(NoiseRequest())))

  def createNoise: Action[AnyContent] =
    Action: request =>
      given MessagesRequest[AnyContent] = request
      noiseForm.bindFromRequest()
        .fold(
          formWithErrors => BadRequest(noiseView(formWithErrors)),
          noiseRequest   =>
            makeSomeNoise(noiseRequest)
            Redirect(routes.NoiseController.noise).flashing("success" -> "Log messages written.")
        )

  private def makeSomeNoise(request: NoiseRequest): Unit =
    for i <- 1 to request.amount do
      request.level match
        case "ERROR" => logger.error(s"$i: " + request.message, RuntimeException(request.message))
        case "WARN"  => logger.warn(s"$i: " + request.message)
        case "INFO"  => logger.info(s"$i: " + request.message)
        case _       => logger.warn("Unrecognized log level")
