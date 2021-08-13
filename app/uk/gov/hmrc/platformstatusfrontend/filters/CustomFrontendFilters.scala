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

package uk.gov.hmrc.platformstatusfrontend.filters

import javax.inject.{Inject, Singleton}
import play.api.http.HttpFilters
import play.api.mvc.EssentialFilter
import uk.gov.hmrc.play.bootstrap.filters.FrontendFilters

@Singleton
class CustomFrontendFilters @Inject()(frontendFilters: FrontendFilters, headerSizeFilter: HeaderSizeFilter) extends HttpFilters {
  override val filters: Seq[EssentialFilter] = {
    // Add our custom filter to measure the byte size of the request headers, *before* any additional headers are added by bootstrap
    headerSizeFilter +: frontendFilters.filters
  }
}