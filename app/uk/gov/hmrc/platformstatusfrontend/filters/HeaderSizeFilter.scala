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

package uk.gov.hmrc.platformstatusfrontend.filters

import akka.stream.Materializer
import javax.inject.Inject
import play.api.mvc._
import uk.gov.hmrc.platformstatusfrontend.util.MeasureUtil.byteSize

import scala.concurrent.{ExecutionContext, Future}

class HeaderSizeFilter @Inject()(implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {

    val headers = requestHeader.headers.headers.map{ case (k,v) => s"$k: $v"}.mkString("\n")
    val headerLength = "Header-Length" -> byteSize(headers).toString

    nextFilter(requestHeader.withHeaders(requestHeader.headers.add(headerLength)))
  }
}