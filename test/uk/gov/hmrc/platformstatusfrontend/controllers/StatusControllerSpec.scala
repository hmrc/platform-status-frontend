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

import org.mockito.scalatest.MockitoSugar
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api._
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.platformstatusfrontend.config.AppConfig
import uk.gov.hmrc.platformstatusfrontend.services.{PlatformStatus, StatusChecker}
import uk.gov.hmrc.platformstatusfrontend.views.html.{Status => StatusView}
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StatusControllerSpec
  extends AnyWordSpec
     with Matchers
     with GuiceOneAppPerSuite
     with MockitoSugar {

  private val fakeRequest = FakeRequest("GET", "/")
  private val env           = Environment.simple()
  private val configuration: Configuration = Configuration.load(env)
  private val appConfig     = new AppConfig(configuration)


  private trait Setup {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val statusChecker = mock[StatusChecker]
    val dummyStatus = PlatformStatus(enabled = true, "name", isWorking = true, "description", Some("No reason"))
    when(statusChecker.iteration1Status())
      .thenReturn(Future.successful(dummyStatus.copy(name = "it1")))
    when(statusChecker.iteration2Status())
      .thenReturn(Future.successful(dummyStatus.copy(name = "it2")))
    when(statusChecker.iteration3Status()(any[HeaderCarrier]))
      .thenReturn(Future.successful(dummyStatus.copy(name = "it3")))
    when(statusChecker.iteration4Status())
      .thenReturn(Future.successful(dummyStatus.copy(name = "it4")))
    when(statusChecker.iteration5Status()(any[HeaderCarrier]))
      .thenReturn(Future.successful(dummyStatus.copy(name = "it5")))

    val statusView: StatusView = app.injector.instanceOf[StatusView]

    val controller = new StatusController(appConfig, stubMessagesControllerComponents(), statusChecker, statusView)
  }

  "GET /" should {
    "return 200" in new Setup() {
      val result = controller.platformStatus(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in new Setup() {
      val result = controller.platformStatus(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }
}
