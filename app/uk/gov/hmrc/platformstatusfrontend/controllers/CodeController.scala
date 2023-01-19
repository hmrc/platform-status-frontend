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

import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import uk.gov.hmrc.platformstatusfrontend.config.AppConfig
import uk.gov.hmrc.platformstatusfrontend.services.MemoryHog
import uk.gov.hmrc.platformstatusfrontend.views.html.{Code, CodeResponse}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}

case class CodeRequest(
  code   : Int    = 200,
  message: String = "a profound message"
)

@Singleton
class CodeController @Inject()(
  appConfig       : AppConfig,
  mcc             : MessagesControllerComponents,
  memoryHog       : MemoryHog,
  codeView        : Code,
  codeResponseView: CodeResponse
) extends FrontendController(mcc) {

  val codeForm: Form[CodeRequest] =
    Form(
      mapping(
        "code" -> number,
        "message"  -> text
      )(CodeRequest.apply)(CodeRequest.unapply)
    )

  def code =
    Action(parse.empty) { implicit request =>
      Ok(codeView(codeForm.fill(CodeRequest())))
    }

  def respondWithCode =
    Action(parse.formUrlEncoded) { implicit request =>
      codeForm.bindFromRequest()
        .fold(
          formWithErrors => BadRequest(codeView(formWithErrors) )
        , codeRequest => {
            if (codeRequest.code == 504)
              Thread.sleep(appConfig.badGatewayTimeout.toMillis)
            new Status(codeRequest.code)(codeResponseView(codeRequest.code, codeRequest.message))
          }
        )
    }
}
