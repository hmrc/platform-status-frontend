/*
 * Copyright 2020 HM Revenue & Customs
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

case class CodeRequest(code: Int = 200, message: String = "a profound message")

@Singleton
class CodeController @Inject()(appConfig: AppConfig, mcc: MessagesControllerComponents, memoryHog: MemoryHog) extends FrontendController(mcc) {

  val codeForm: Form[CodeRequest] = Form(
    mapping(
      "code" -> number,
      "message"  -> text
    )(CodeRequest.apply)(CodeRequest.unapply)
  )

  implicit val config: AppConfig = appConfig
  val logger: Logger = Logger(this.getClass)

  def code = Action.async { implicit request =>
    Future.successful( Ok(views.html.code( codeForm.fill(CodeRequest()))  ) )
  }

  def respondWithCode = Action { implicit request =>
    codeForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest( views.html.code(formWithErrors) )
      },
      codeRequest => {
        if (codeRequest.code == 504) {
          Thread.sleep(config.badGatewayTimeout.toMillis)
        }
        new Status(codeRequest.code)(views.html.codeResponse(codeRequest.code, codeRequest.message))
      }
    )
  }

}

