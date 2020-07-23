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
import play.api.data.Form
import play.api.data.Forms.{mapping, _}
import play.api.mvc._
import uk.gov.hmrc.platformstatusfrontend.config.AppConfig
import uk.gov.hmrc.platformstatusfrontend.services.MeasureService
import uk.gov.hmrc.platformstatusfrontend.util.MeasureUtil._
import uk.gov.hmrc.platformstatusfrontend.views
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

case class MeasureRequest(headerName: String = "", bytes: Int = 0)

@Singleton
class MeasureController @Inject()(appConfig: AppConfig,
                                  mcc: MessagesControllerComponents,
                                  measureService: MeasureService
                                 )(implicit executionContext: ExecutionContext)
extends FrontendController(mcc){

  val logger: Logger = Logger(this.getClass)

  val measureForm: Form[MeasureRequest] = Form(
    mapping(
      "headerName" -> text,
      "bytes" -> number
    )(MeasureRequest.apply)(MeasureRequest.unapply)
  )

  implicit val config: AppConfig = appConfig

  def measure = Action.async { implicit request =>
    Future.successful( Ok(views.html.measure(measureForm.fill(MeasureRequest()))))
  }

  def measureHeader = Action.async { implicit request =>
    // This custom header was added to the request by our custom filters, so just pull out its value
    val headerLength = request.headers.get(X_HEADER_LENGTH).map(s => s"$s bytes").getOrElse(s"? Unknown, was not able to extract injected $X_HEADER_LENGTH header")
    logger.info(s"Received message with header length: $headerLength")
    Future.successful(Ok(s"Total size of all headers received: $headerLength"))
  }

  def measureBody = Action.async { implicit request =>
    val bodyLength = request.headers.get(CONTENT_LENGTH).map(s => s"$s bytes").getOrElse(s"? Unknown, $CONTENT_LENGTH header was not found")
    logger.info(s"Received message with body length: $bodyLength")
    Future.successful(Ok(s"Body length received: $bodyLength"))
  }

  def randomResponseHeaderOfSize() = Action.async { implicit request =>
    measureForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.measure(formWithErrors)))
      },
      measureRequest => {
        val generated = generateStringOfSize(measureRequest.bytes)
        val headerName = measureRequest.headerName
        logger.info(s"Generated random content for header '$headerName' of ${measureRequest.bytes} bytes to send in response")
        Future.successful(Ok(s"Response header ${measureRequest.headerName} filled with ${measureRequest.bytes} random bytes").withHeaders(headerName -> generated))
      }
    )
  }

  def randomResponseBodyOfSize() = Action.async { implicit request =>
    measureForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.measure(formWithErrors)))
      },
      measureRequest => {
        val generated = generateStringOfSize(measureRequest.bytes)
        logger.info(s"Generated random body of ${measureRequest.bytes} bytes")
        Future.successful(Ok(s"$generated"))
      }
    )
  }

  def headerOfSizeToBackend() = Action.async { implicit request =>
    measureForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.measure(formWithErrors)))
      },
      measureRequest => {
        val generated = generateStringOfSize(measureRequest.bytes)
        logger.info(s"Generated random content for header '${measureRequest.headerName}' of ${measureRequest.bytes} bytes to send to backend")
        measureService.headerToBackend(generated, measureRequest.headerName).map(Ok(_))
      }
    )
  }

  def bodyOfSizeToBackend() = Action.async { implicit request =>
    measureForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.measure(formWithErrors)))
      },
      measureRequest => {
        val generated = generateStringOfSize(measureRequest.bytes)
        logger.info(s"Generated random body of ${measureRequest.bytes} bytes to send to backend")
        measureService.bodyToBackend(generated).map(Ok(_))
      }
    )
  }

}


