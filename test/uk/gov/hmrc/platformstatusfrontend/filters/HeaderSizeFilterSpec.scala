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

import org.apache.pekko.stream.Materializer
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import play.api.mvc.Results.Ok
import play.api.mvc.{Action, AnyContent, DefaultActionBuilder, Headers}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.platformstatusfrontend.util.Generators.*
import uk.gov.hmrc.platformstatusfrontend.util.MeasureUtil

class HeaderSizeFilterSpec
   extends AnyWordSpec
      with Matchers
      with GuiceOneAppPerSuite
      with ScalaCheckDrivenPropertyChecks
      with ScalaFutures:

  private implicit lazy val materializer: Materializer = app.materializer

  val filter = HeaderSizeFilter()
  val act: DefaultActionBuilder = app.injector.instanceOf[DefaultActionBuilder]
  val action: Action[AnyContent] = act:
    request =>
      Ok("").withHeaders(request.headers.headers: _*)

  "HeaderSizeFilterSpec" should:
    "add an X_HEADER_LENGTH header with value 0 if there are no headers on the request" in:
      val headers = Headers.create()

      val request = FakeRequest(POST, "/").withHeaders(headers)
      val result = filter(action)(request).run()

      status(result) shouldBe OK
      header(MeasureUtil.X_HEADER_LENGTH, result) shouldBe Some("0")

    "add an X_HEADER_LENGTH header with value of the header size in bytes" in:
      val headers = Headers("SOME_HEADER" -> "my value")

      val request = FakeRequest(POST, "/").withHeaders(headers)
      val result = filter(action)(request).run()

      val expectedSize =
        MeasureUtil.byteSize("SOME_HEADER: my value".stripMargin)

      status(result) shouldBe OK
      header(MeasureUtil.X_HEADER_LENGTH, result) shouldBe Some(expectedSize.toString)

    "add an X_HEADER_LENGTH header with value of the header size in bytes for multiple headers" in:
      forAll(headersGen):
        rawHeaders =>
          val headers = Headers(rawHeaders : _*)

          val request = FakeRequest(POST, "/").withHeaders(headers)
          val result = filter(action)(request).run()

          val expectedSize =
            MeasureUtil.byteSize(rawHeaders.map { case (k, v) => s"$k: $v" }.mkString("\r\n"))

          status(result) shouldBe OK
          header(MeasureUtil.X_HEADER_LENGTH, result) shouldBe Some(expectedSize.toString)
