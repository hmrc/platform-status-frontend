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

import play.api.libs.concurrent.Futures
import play.api.mvc._
import uk.gov.hmrc.platformstatusfrontend.config.AppConfig
import uk.gov.hmrc.platformstatusfrontend.models.{GcInformation, GcSummary}
import uk.gov.hmrc.platformstatusfrontend.services.GarbageService
import uk.gov.hmrc.platformstatusfrontend.views.html.Garbage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions


@Singleton
class GarbageController @Inject()(appConfig: AppConfig, mcc: MessagesControllerComponents, val garbageService: GarbageService, garbageView: Garbage)(implicit executionContext: ExecutionContext, futures: Futures)
  extends FrontendController(mcc) {

  implicit val config: AppConfig = appConfig


  def garbage: Action[AnyContent] = Action.async { implicit request =>

    val properties = System.getProperties.asScala
    for ((k,v) <- properties) println(s"key: $k, value: $v")

    val gcSummaryFuture = for {
      backend <- garbageService.getBackendGcInfo
      frontend <- garbageService.getFrontendGcInfo
    } yield GcSummary(frontend, backend)

    gcSummaryFuture.map{gcSummary => Ok(garbageView(gcSummary))}
  }

}


