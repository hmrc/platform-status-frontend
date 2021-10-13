/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.platformstatusfrontend.services

import com.google.inject.Inject
import play.api.Logger
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.platformstatusfrontend.config.AppConfig
import uk.gov.hmrc.platformstatusfrontend.connectors.BackendConnector
import uk.gov.hmrc.platformstatusfrontend.models.GcInformation

import java.lang.management.GarbageCollectorMXBean
import javax.inject.Singleton
import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GarbageService @Inject()(backendConnector: BackendConnector, appConfig: AppConfig) {

  private val logger = Logger(this.getClass)

  def getBackendGcInfo(implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[GcInformation] = {
    backendConnector.gcInformation.recoverWith {
      case ex: Exception => {
        val msg = s"bodyToBackend call to backend service failed"
        logger.warn(msg, ex)
        Future(GcInformation(-1, Seq[GarbageCollectorMXBean]()))
      }
    }
  }

  def getFrontendGcInfo: Future[GcInformation] = {

    import java.lang.management.ManagementFactory

    val gBeans = ManagementFactory.getGarbageCollectorMXBeans.asScala.toList

    val coreCount = Runtime.getRuntime.availableProcessors
    Future.successful(GcInformation(coreCount, gBeans))
  }
}
