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

import play.api.data.{Form, Forms}
import play.api.mvc.*
import uk.gov.hmrc.platformstatusfrontend.config.AppConfig
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
  codeView        : Code,
  codeResponseView: CodeResponse
) extends FrontendController(mcc):

  val codeForm: Form[CodeRequest] =
    Form(
      Forms.mapping(
        "code"    -> Forms.number,
        "message" -> Forms.text
      )(CodeRequest.apply)(o => Some(Tuple.fromProductTyped(o)))
    )

  def code: Action[Unit] =
    Action(parse.empty): request =>
      given MessagesRequestHeader = request
      Ok(codeView(codeForm.fill(CodeRequest())))

  def respondWithCode: Action[Map[String, Seq[String]]] =
    Action(parse.formUrlEncoded): request =>
      given MessagesRequest[Map[String, Seq[String]]] = request
      codeForm.bindFromRequest()
        .fold(
          formWithErrors => BadRequest(codeView(formWithErrors)),
          codeRequest =>
            if codeRequest.code == 504 then
              Thread.sleep(appConfig.badGatewayTimeout.toMillis)
            Status(codeRequest.code)(codeResponseView(codeRequest.code, codeRequest.message))
        )
