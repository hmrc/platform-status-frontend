/*
 * Copyright 2025 HM Revenue & Customs
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

import play.api.data.*
import play.api.data.Forms.*
import play.api.mvc.*
import uk.gov.hmrc.platformstatusfrontend.config.AppConfig
import uk.gov.hmrc.platformstatusfrontend.services.CurlService
import uk.gov.hmrc.platformstatusfrontend.views.html.{Curl, CurlResponse}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class CurlRequest(
                        requestType: String = "GET",
                        url: String = ""
                      )

@Singleton
class CurlController @Inject()(
                                appConfig: AppConfig,
                                mcc: MessagesControllerComponents,
                                curlView: Curl,
                                curlResponseView: CurlResponse,
                                curlService: CurlService
                              )(using
                                ec: ExecutionContext
                              ) extends FrontendController(mcc):

  val curlForm: Form[CurlRequest] = {
    Form(
      mapping(
        "requestType" -> text.verifying("Request type must be either GET or POST", rt => rt == "GET" || rt == "POST"),
        "url" -> nonEmptyText
      )(CurlRequest.apply)(o => Some(Tuple.fromProductTyped(o)))
    )
  }

  def curl: Action[Unit] = {
    Action(parse.empty):
      implicit request =>
        Ok(curlView(curlForm.fill(CurlRequest())))
  }

  def curlCall: Action[Map[String, Seq[String]]] = {
    Action.async(parse.formUrlEncoded):
      implicit request =>
        curlForm.bindFromRequest()
          .fold(
            formWithErrors => {
              Future.successful(BadRequest(curlView(formWithErrors)))
            },
            curlRequest => curlService.makeCurlRequest(curlRequest).map { resp =>
              Ok(curlResponseView(resp.status.toString, resp.body))
            }
          )
  }
