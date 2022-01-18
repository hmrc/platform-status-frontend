/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.data.Form
import play.api.data.Forms.{mapping, number, text}
import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.platformstatusfrontend.config.AppConfig
import uk.gov.hmrc.platformstatusfrontend.models.AuditVolumeRequest
import uk.gov.hmrc.platformstatusfrontend.services.AuditVolumeService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.platformstatusfrontend.views.html.AuditVolume

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class AuditVolumeController @Inject()(service: AuditVolumeService,
                                      view: AuditVolume,
                                      mcc: MessagesControllerComponents)
                                     (implicit config: AppConfig)
  extends FrontendController(mcc) {

  val form: Form[AuditVolumeRequest] = Form(
    mapping(
      "auditType" -> text,
      "n" -> number
    )(AuditVolumeRequest.apply)(AuditVolumeRequest.unapply)
  )

  def setup() = Action.async { implicit request =>
    Future.successful( Ok(view(form.fill(AuditVolumeRequest()))))
  }

  def run() = Action.async { implicit  request =>
    form.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(view(formWithErrors)))
      },
      measureRequest => {
        service.sendAuditMessages(measureRequest.auditType, measureRequest.n)
        Future.successful(Ok("Generated"))
      }
    )
  }
}