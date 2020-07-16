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
class MeasureController @Inject()(appConfig: AppConfig, mcc: MessagesControllerComponents, val measureService: MeasureService)(implicit executionContext: ExecutionContext)
extends FrontendController(mcc){

  val logger: Logger = Logger(this.getClass)

  def measureBody = Action.async { implicit request =>
    val bodyLength = request.headers.get(CONTENT_LENGTH).map(_.toInt).getOrElse(-1)
    logger.info(s"Received message with body length: $bodyLength bytes")
    Future.successful(Ok(s"Body received: $bodyLength bytes"))
  }

  def measureHeader = Action.async { implicit request =>
    val headerLength = request.headers.get("Header-Length").map(_.toInt).getOrElse(-1)
    logger.info(s"Received message with header length: $headerLength bytes")
    Future.successful(Ok(s"Header received: $headerLength bytes"))
  }

  def randomBodyOfSize(size: Int) = Action.async { implicit request =>
    val generated = generateStringOfSize(size)
    logger.info(s"Generated random body of $size bytes")
    Future.successful(Ok(s"$generated"))
  }

  def bodyOfSizeToBackend(size: Int) = Action.async { implicit request =>
    val generated = generateStringOfSize(size)
    logger.info(s"Generated random body of $size bytes to send to backend")
    measureService.bodyToBackend(generated).map(Ok(_))
  }

  def headerOfSizeToBackend(size: Int, headerName: String) = Action.async { implicit request =>
    val generated = generateStringOfSize(size)
    logger.info(s"Generated random content for header '$headerName' of $size bytes to send to backend")
    measureService.headerToBackend(generated, headerName).map(Ok(_))
  }

}


