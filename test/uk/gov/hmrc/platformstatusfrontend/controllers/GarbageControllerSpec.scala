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

import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers.any
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.*
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.platformstatusfrontend.models.GcInformation
import uk.gov.hmrc.platformstatusfrontend.services.GarbageService
import uk.gov.hmrc.platformstatusfrontend.views.html.Garbage as GarbageView
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

import java.lang.management.GarbageCollectorMXBean
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GarbageControllerSpec
  extends AnyWordSpec
     with Matchers
     with GuiceOneAppPerSuite
     with MockitoSugar
     with BeforeAndAfterEach:

  private val fakeRequest = FakeRequest("GET", "/")

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .build()

  private trait Setup:
    given HeaderCarrier = HeaderCarrier()

    private val garbageService = mock[GarbageService]
    private val dummyInfo = GcInformation(1, Seq[GarbageCollectorMXBean]())
    when(garbageService.getBackendGcInfo()(using any)).thenReturn(Future.successful(dummyInfo))
    when(garbageService.getFrontendGcInfo()).thenReturn(Future.successful(dummyInfo))

    private val garbageView = app.injector.instanceOf[GarbageView]

    val controller = GarbageController(stubMessagesControllerComponents(), garbageService, garbageView)

  "GET /" should:
    "return 200" in new Setup():
      val result = controller.garbage(fakeRequest)
      status(result) shouldBe Status.OK

    "return HTML" in new Setup():
      val result = controller.garbage(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
