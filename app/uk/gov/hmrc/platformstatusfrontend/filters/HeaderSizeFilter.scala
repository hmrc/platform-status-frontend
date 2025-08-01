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

package uk.gov.hmrc.platformstatusfrontend.filters

import play.api.mvc.*
import uk.gov.hmrc.platformstatusfrontend.util.MeasureUtil.{X_HEADER_LENGTH, byteSize}

import javax.inject.Inject

// We're still investigating the why, but have found that the MDCFilter only applies the data correctly when the first filter in the enabled list is
// an EssentialFilter.  By default, the first filter for frontend services is the SecurityHeadersFilter.
// In this instance we want the HeaderSizeFilter to be the first, therefore we must ensure that it is an EssentialFilter.  When we get to the bottom
// of the issue, we should be able to revert the HeaderSizeFilter back to extend the Filter wrapper and still see the MDC data in the logs.
class HeaderSizeFilter @Inject() extends EssentialFilter:

  override def apply(nextFilter: EssentialAction): EssentialAction =
    requestHeader =>
      val allHeaders = requestHeader.headers.headers

      // Reconstructing the headers as they would be in the HTTP spec for the purpose of calculating the byte size
      // N.B. Would be nice to just access the raw request bytes and measure the size up to the first blank line (after headers),
      // but doesn't look possible to access the raw request bytes (not just the raw *body* bytes).
      val headers = allHeaders.map{ case (k,v) => s"$k: $v"}.mkString("\r\n")

      // Add a custom header to the request just for convenience that we can pull out later
      val headerLength = X_HEADER_LENGTH -> byteSize(headers).toString

      nextFilter(requestHeader.withHeaders(requestHeader.headers.add(headerLength)))
