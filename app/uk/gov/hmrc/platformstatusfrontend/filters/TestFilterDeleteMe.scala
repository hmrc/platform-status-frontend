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

import akka.stream.Materializer
import play.api.mvc.Call
import uk.gov.hmrc.allowlist.AkamaiAllowlistFilter

import javax.inject.Inject

class TestFilterDeleteMe @Inject() (materializer: Materializer) extends AkamaiAllowlistFilter{
  override def allowlist: Seq[String] = Seq.empty

  override def destination: Call = Call("GET", "http://www.gov.uk")

  override implicit def mat: Materializer = materializer
}
