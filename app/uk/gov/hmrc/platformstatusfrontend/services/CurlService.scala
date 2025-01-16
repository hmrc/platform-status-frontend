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

package uk.gov.hmrc.platformstatusfrontend.services

import play.api.Logging
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.platformstatusfrontend.connectors.CurlConnector
import uk.gov.hmrc.platformstatusfrontend.controllers.{CurlController, CurlRequest}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CurlService @Inject()(curlConnector: CurlConnector)(using ec: ExecutionContext) extends Logging:

  def makeCurlRequest(curlRequest: CurlRequest)(using hc: HeaderCarrier): Future[HttpResponse] =
    curlConnector.callWebService(curlRequest).recoverWith { ex =>
      logger.error("Webservice call failed: " + ex.getMessage, ex)
      Future.successful(HttpResponse(
        status = 419,
        body = s"Error occurred inside Scala: ${ex.getMessage}"
      ))
    }
