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

package uk.gov.hmrc.platformstatusfrontend.connectors

import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}
import uk.gov.hmrc.platformstatusfrontend.models.GcInformation
import uk.gov.hmrc.platformstatusfrontend.services.PlatformStatus
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BackendConnector @Inject()(
  http          : HttpClient,
  servicesConfig: ServicesConfig
)(implicit
   ec: ExecutionContext
) {
  import HttpReads.Implicits._

  private val backendBaseUrl: String =
    s"${servicesConfig.baseUrl("platform-status-backend")}/platform-status-backend"

  def iteration3Status()(implicit hc: HeaderCarrier): Future[PlatformStatus] =
    http.GET[PlatformStatus](s"$backendBaseUrl/status/iteration3")

  def iteration5Status()(implicit hc: HeaderCarrier): Future[PlatformStatus] =
    http.GET[PlatformStatus](s"$backendBaseUrl/status/iteration5")

  def measure(content: String, headers: Seq[(String, String)] = Seq.empty)(implicit hc: HeaderCarrier): Future[String] =
    http.POSTString[HttpResponse](s"$backendBaseUrl/measure", content, headers).map(_.body)

  def gcInformation()(implicit hc: HeaderCarrier): Future[GcInformation] =
    http.GET[GcInformation](s"$backendBaseUrl/gcinfo")
}
