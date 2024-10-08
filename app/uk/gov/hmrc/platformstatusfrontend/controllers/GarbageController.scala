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

import play.api.mvc._
import uk.gov.hmrc.platformstatusfrontend.models.GcSummary
import uk.gov.hmrc.platformstatusfrontend.services.GarbageService
import uk.gov.hmrc.platformstatusfrontend.views.html.Garbage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters._
import play.api.Logger


@Singleton
class GarbageController @Inject()(
  mcc           : MessagesControllerComponents,
  garbageService: GarbageService,
  garbageView   : Garbage
)(using
  ec: ExecutionContext
) extends FrontendController(mcc):

  private val logger = Logger(this.getClass)

  def garbage: Action[AnyContent] =
    Action.async:
      implicit request =>
        val properties = System.getProperties.asScala
        for (k,v) <- properties do logger.debug(s"key: $k, value: $v")
  
        val gcSummaryFuture = for
          backend  <- garbageService.getBackendGcInfo()
          frontend <- garbageService.getFrontendGcInfo()
        yield GcSummary(frontend, backend)
  
        gcSummaryFuture.map(gcSummary => Ok(garbageView(gcSummary)))
