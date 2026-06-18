/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.platformstatusfrontend.controllers

import com.github.tomakehurst.wiremock.client.WireMock.*
import org.scalatest.OptionValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status.{FORBIDDEN, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, defaultAwaitTimeout, route, status, writeableOf_AnyContentAsEmpty}
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.http.test.WireMockSupport

class InternalAuthControllerITSpec
  extends AnyWordSpec
     with Matchers
     with OptionValues
     with GuiceOneAppPerSuite
     with WireMockSupport:

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .configure(
        "microservice.services.internal-auth.host" -> wireMockHost,
        "microservice.services.internal-auth.port" -> wireMockPort
      )
      .build()

  "Platform status routes" should:
    "authorize application pages with internal-auth" in:
      stubFor(
        post(urlEqualTo("/internal-auth/auth"))
          .willReturn(okJson("""{"retrievals": []}"""))
      )

      val result =
        route(
          app,
          FakeRequest(GET, "/platform-status/noise")
            .withSession(SessionKeys.authToken -> "Token token")
        ).value

      status(result) shouldBe OK
      verify(
        postRequestedFor(urlEqualTo("/internal-auth/auth"))
          .withRequestBody(containing("platform-status-frontend"))
          .withRequestBody(containing("READ"))
      )

    "leave the access denied page outside internal-auth" in:
      val result =
        route(app, FakeRequest(GET, "/platform-status/denied")).value

      status(result) shouldBe FORBIDDEN
      verify(0, postRequestedFor(urlEqualTo("/internal-auth/auth")))
