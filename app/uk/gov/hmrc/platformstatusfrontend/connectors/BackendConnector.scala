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

import play.api.libs.ws.DefaultBodyWritables

import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.platformstatusfrontend.models.GcInformation
import uk.gov.hmrc.platformstatusfrontend.services.PlatformStatus
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BackendConnector @Inject()(
  http          : HttpClientV2,
  servicesConfig: ServicesConfig
)(using
  ec: ExecutionContext
):
  import HttpReads.Implicits._
  import DefaultBodyWritables.writeableOf_String

  private val backendBaseUrl: String =
    s"${servicesConfig.baseUrl("platform-status-backend")}/platform-status-backend"

  def iteration3Status()(using hc: HeaderCarrier): Future[PlatformStatus] =
    http.get(url"$backendBaseUrl/status/iteration3")
      .execute[PlatformStatus]

  def iteration5Status()(using hc: HeaderCarrier): Future[PlatformStatus] =
    http.get(url"$backendBaseUrl/status/iteration5")
      .execute[PlatformStatus]

  def measure(content: String, headers: Seq[(String, String)] = Seq.empty)(using hc: HeaderCarrier): Future[String] =
    http.post(url"$backendBaseUrl/measure")
      .withBody(content)
      .setHeader(headers :_*)
      .execute[String]

  def gcInformation()(using hc: HeaderCarrier): Future[GcInformation] =
    http.get(url"$backendBaseUrl/gcinfo")
      .execute[GcInformation]
