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
import uk.gov.hmrc.platformstatusfrontend.services.MeasureService
import uk.gov.hmrc.platformstatusfrontend.util.MeasureUtil.{generateStringOfSize, X_HEADER_LENGTH}
import uk.gov.hmrc.platformstatusfrontend.views.html.Measure

import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

case class MeasureRequest(
  headerName: String = "",
  bytes     : Int    = 0
)

@Singleton
class MeasureController @Inject()(
  mcc           : MessagesControllerComponents,
  measureService: MeasureService,
  measureView   : Measure
)(using
  ec: ExecutionContext
) extends FrontendController(mcc):
  private val logger: Logger = Logger(this.getClass)

  val measureForm: Form[MeasureRequest] =
    Form(
      Forms.mapping(
        "headerName" -> Forms.text,
        "bytes"      -> Forms.number
      )(MeasureRequest.apply)(o => Some(Tuple.fromProductTyped(o)))
    )

  def measure: Action[AnyContent] =
    Action: request =>
      given MessagesRequestHeader = request
      Ok(measureView(measureForm.fill(MeasureRequest())))

  def measureHeader: Action[AnyContent] =
    Action: request =>
      // This custom header was added to the request by our custom filters, so just pull out its value
      val headerLength = request.headers.get(X_HEADER_LENGTH).map(s => s"$s bytes").getOrElse(s"? Unknown, was not able to extract injected $X_HEADER_LENGTH header")
      logger.info(s"Received message with header length: $headerLength")
      Ok(s"Total size of all headers received: $headerLength")

  def measureBody: Action[AnyContent] =
    Action: request =>
      val bodyLength = request.headers.get(CONTENT_LENGTH).map(s => s"$s bytes").getOrElse(s"? Unknown, $CONTENT_LENGTH header was not found")
      logger.info(s"Received message with body length: $bodyLength")
      Ok(s"Body length received: $bodyLength")

  def randomResponseHeaderOfSize: Action[AnyContent] =
    Action: request =>
      given MessagesRequest[AnyContent] = request
      measureForm.bindFromRequest()
        .fold(
          formWithErrors => BadRequest(measureView(formWithErrors)),
          measureRequest =>
            val generated = generateStringOfSize(measureRequest.bytes)
            val headerName = measureRequest.headerName
            logger.info(s"Generated random content for header '$headerName' of ${measureRequest.bytes} bytes to send in response")
            Ok(s"Response header ${measureRequest.headerName} filled with ${measureRequest.bytes} random bytes").withHeaders(headerName -> generated)
        )

  def randomResponseBodyOfSize: Action[AnyContent] =
    Action: request =>
      given MessagesRequest[AnyContent] = request
      measureForm.bindFromRequest()
        .fold(
          formWithErrors => BadRequest(measureView(formWithErrors)),
          measureRequest =>
            val generated = generateStringOfSize(measureRequest.bytes)
            logger.info(s"Generated random body of ${measureRequest.bytes} bytes")
            Ok(s"$generated")
        )

  def headerOfSizeToBackend: Action[AnyContent] =
    Action.async: request =>
      given MessagesRequest[AnyContent] = request
      measureForm.bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(measureView(formWithErrors))),
          measureRequest =>
            val generated = generateStringOfSize(measureRequest.bytes)
            logger.info(s"Generated random content for header '${measureRequest.headerName}' of ${measureRequest.bytes} bytes to send to backend")
            measureService.headerToBackend(generated, measureRequest.headerName).map(Ok(_))
        )

  def bodyOfSizeToBackend: Action[AnyContent] =
    Action.async: request =>
      given MessagesRequest[AnyContent] = request
      measureForm.bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(measureView(formWithErrors))),
          measureRequest =>
            val generated = generateStringOfSize(measureRequest.bytes)
            logger.info(s"Generated random body of ${measureRequest.bytes} bytes to send to backend")
            measureService.bodyToBackend(generated).map(Ok(_))
        )
