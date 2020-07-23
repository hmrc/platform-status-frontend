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
import play.api.Logger
import play.api.mvc._
import uk.gov.hmrc.platformstatusfrontend.util.MeasureUtil.byteSize
import uk.gov.hmrc.platformstatusfrontend.util.MeasureUtil.X_HEADER_LENGTH

import scala.concurrent.{ExecutionContext, Future}

class HeaderSizeFilter @Inject()(implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  val logger: Logger = Logger(this.getClass)

  def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    val allHeaders = requestHeader.headers.headers

    logger.info(s"Calculating byte size of ${allHeaders.size} received request headers: ${allHeaders.mkString(",")}")

    // Reconstructing the headers as they would be in the HTTP spec for the purpose of calculating the byte size
    // N.B. Would be nice to just access the raw request bytes and measure the size up to the first blank line (after headers),
    // but doesn't look possible to access the raw request bytes (not just the raw *body* bytes).
    val headers = allHeaders.map{ case (k,v) => s"$k: $v"}.mkString("\r\n")

    // Add a custom header to the request just for convenience that we can pull out later
    val headerLength = X_HEADER_LENGTH -> byteSize(headers).toString

    nextFilter(requestHeader.withHeaders(requestHeader.headers.add(headerLength)))
  }
}