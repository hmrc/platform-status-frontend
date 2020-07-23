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
import play.api.mvc._
import uk.gov.hmrc.platformstatusfrontend.config.AppConfig
import uk.gov.hmrc.platformstatusfrontend.services.MeasureService
import uk.gov.hmrc.platformstatusfrontend.util.MeasureUtil._
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MeasureController @Inject()(appConfig: AppConfig,
                                  mcc: MessagesControllerComponents,
                                  measureService: MeasureService
                                 )(implicit executionContext: ExecutionContext)
extends FrontendController(mcc){

  val logger: Logger = Logger(this.getClass)

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

  def randomResponseHeaderOfSize(size: Int, headerName: String) = Action.async { implicit request =>
    val generated = generateStringOfSize(size)
    logger.info(s"Generated random content for header '$headerName' of $size bytes to send in response")
    Future.successful(Ok(s"Response header $headerName filled with $size random bytes").withHeaders(headerName -> generated))
  }

  def randomBodyOfSize(size: Int) = Action.async { implicit request =>
    val generated = generateStringOfSize(size)
    logger.info(s"Generated random body of $size bytes")
    Future.successful(Ok(s"$generated"))
  }

  def headerOfSizeToBackend(size: Int, headerName: String) = Action.async { implicit request =>
    val generated = generateStringOfSize(size)
    logger.info(s"Generated random content for header '$headerName' of $size bytes to send to backend")
    measureService.headerToBackend(generated, headerName).map(Ok(_))
  }

  def bodyOfSizeToBackend(size: Int) = Action.async { implicit request =>
    val generated = generateStringOfSize(size)
    logger.info(s"Generated random body of $size bytes to send to backend")
    measureService.bodyToBackend(generated).map(Ok(_))
  }

}


