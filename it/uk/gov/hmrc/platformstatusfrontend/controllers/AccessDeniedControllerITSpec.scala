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

package uk.gov.hmrc.platformstatusfrontend.controllers

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.http.Status.{FORBIDDEN,OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.test.Helpers.{await, defaultAwaitTimeout}

class AccessDeniedControllerITSpec extends AnyWordSpec with Matchers with GuiceOneServerPerSuite {

  private val serviceUrl: String = s"http://localhost:$port/platform-status"
  private val TRUE_CLIENT_IP_HEADER = "True-Client-Ip"
  private lazy val ALLOWED_IP_ADDRESS = "10.0.0.1"

  private  lazy val wsClient: WSClient = app.injector.instanceOf[WSClient]

  private lazy val config: Map[String, String] = Map(
    "bootstrap.filters.allowlist.enabled" -> "true",
    "bootstrap.filters.allowlist.ips" -> ALLOWED_IP_ADDRESS
    )

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .configure(config)
    .build()


  "With the Allowlist filter enabled" when {
    s"a request is made with an ip in the $TRUE_CLIENT_IP_HEADER which is not on the allowlist it" should {
      "return 403 Forbidden" in {
        lazy val result: WSResponse = {
          await(wsClient.url(serviceUrl).withFollowRedirects(true).withHttpHeaders(
            Seq(TRUE_CLIENT_IP_HEADER -> "192.168.2.1"): _*).get())
        }
        result.status shouldBe FORBIDDEN
      }
    }
    s"a request is made with an ip in the $TRUE_CLIENT_IP_HEADER which is on the allowlist it" should {
      "return a 200 OK" in {
        lazy val result: WSResponse = {
          await(wsClient.url(serviceUrl).withFollowRedirects(true).withHttpHeaders(
            Seq(TRUE_CLIENT_IP_HEADER -> ALLOWED_IP_ADDRESS): _*).get())
        }
        result.status shouldBe OK
      }
    }
  }
}
