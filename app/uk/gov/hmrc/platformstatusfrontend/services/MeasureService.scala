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

package uk.gov.hmrc.platformstatusfrontend.services

import com.google.inject.Inject
import javax.inject.Singleton
import play.api.Logger
import play.api.libs.concurrent.Timeout
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.platformstatusfrontend.config.AppConfig
import uk.gov.hmrc.platformstatusfrontend.connectors.BackendConnector

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MeasureService @Inject()(backendConnector: BackendConnector, appConfig: AppConfig) extends Timeout {

  val logger = Logger(this.getClass)

  def bodyToBackend(content: String)(implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[String] = {
    backendConnector.measure(content).recoverWith {
      case ex: Exception => {
        val msg = s"bodyToBackend call to backend service failed"
        logger.warn(msg, ex)
        Future(s"$msg: ${ex.getMessage}")
      }
    }
  }

  def headerToBackend(content: String, headerName: String)(implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[String] = {
    backendConnector.measure("", Seq(headerName -> content, "Test-Header-Name" -> headerName)).recoverWith {
      case ex: Exception => {
        val msg = s"headerToBackend call to backend service failed"
        logger.warn(msg, ex)
        Future(s"$msg: ${ex.getMessage}")
      }
    }
  }

}