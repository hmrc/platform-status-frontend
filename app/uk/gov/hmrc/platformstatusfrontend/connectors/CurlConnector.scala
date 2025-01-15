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

package uk.gov.hmrc.platformstatusfrontend.connectors

import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, StringContextOps}
import uk.gov.hmrc.platformstatusfrontend.controllers.CurlRequest

import java.net.URL
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CurlConnector @Inject()(http: HttpClientV2)(using ec: ExecutionContext):

  import HttpReads.Implicits.*

  def callWebService(curlRequest: CurlRequest)(using hc: HeaderCarrier): Future[HttpResponse] =
    try
      curlRequest match
        case CurlRequest(requestType, url) if requestType == "GET" => http.get(url"$url").execute
        case CurlRequest(requestType, url) if requestType == "POST" => http.post(url"$url").execute
    catch
      case ex: Exception => Future.successful(HttpResponse(
        status = 419,
        body = s"Error occurred inside Scala: ${ex.getMessage}"
      ))