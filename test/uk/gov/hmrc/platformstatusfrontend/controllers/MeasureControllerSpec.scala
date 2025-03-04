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

import org.apache.pekko.stream.Materializer
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.mockito.ArgumentCaptor
import org.scalacheck.Gen
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import play.api.*
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.platformstatusfrontend.connectors.BackendConnector
import uk.gov.hmrc.platformstatusfrontend.services.MeasureService
import uk.gov.hmrc.platformstatusfrontend.util.Generators.*
import uk.gov.hmrc.platformstatusfrontend.util.MeasureUtil.*
import uk.gov.hmrc.platformstatusfrontend.views.html.Measure
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents
import play.api.test.CSRFTokenHelper.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MeasureControllerSpec
   extends AnyWordSpec
      with Matchers
      with GuiceOneAppPerSuite
      with MockitoSugar
      with ScalaCheckDrivenPropertyChecks
      with ScalaFutures:

  private implicit lazy val materializer: Materializer = app.materializer

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .build()

  private trait Setup:
    given HeaderCarrier = HeaderCarrier()

    val backendConnector: BackendConnector = mock[BackendConnector]

    private val measureService: MeasureService = MeasureService(backendConnector)
    private val measureView   : Measure = app.injector.instanceOf[Measure]

    val controller = MeasureController(stubMessagesControllerComponents(), measureService, measureView)


  "GET /measure-header" should:
    "return unknown size if the HeaderSizeFilter hasn't added the length header to the incoming request" in new Setup():
      val result = controller.measureHeader()(FakeRequest())

      status(result) shouldBe Status.OK
      contentAsString(result) shouldBe "Total size of all headers received: ? Unknown, was not able to extract injected X-Header-Length header"

    "return length of headers present on request, as calculated by the HeaderSizeFilter" in new Setup():
      val result = controller.measureHeader()(FakeRequest().withHeaders(X_HEADER_LENGTH -> "100"))

      status(result) shouldBe Status.OK
      contentAsString(result) shouldBe "Total size of all headers received: 100 bytes"


  "POST /measure-body" should:
    "return unknown size if the Content-Length header is not found" in new Setup():
      val result = controller.measureBody()(FakeRequest(POST, "/"))

      status(result) shouldBe Status.OK
      contentAsString(result) shouldBe "Body length received: ? Unknown, Content-Length header was not found"

    "return length of body present on request, as defined by the Content-Length header" in new Setup():
      val result = controller.measureBody()(FakeRequest(POST, "/").withHeaders(CONTENT_LENGTH -> "100"))

      status(result) shouldBe Status.OK
      contentAsString(result) shouldBe "Body length received: 100 bytes"


  "GET /measure-random-header" should:
    "return a response with a header of the specified name filled with random bytes to the specified length" in new Setup():
      forAll(Gen.choose(1, 1000000), nonEmptyString):
        (bytes, headerName) =>
          val result = controller.randomResponseHeaderOfSize()(FakeRequest(GET, s"/?bytes=$bytes&headerName=$headerName").withCSRFToken)

          status(result) shouldBe Status.OK
          headers(result).get(headerName).map(byteSize) shouldBe Some(bytes)
          contentAsString(result) shouldBe s"Response header $headerName filled with $bytes random bytes"


  "GET /measure-random-body" should:
    "return a response with a body filled with random bytes to the specified length" in new Setup():
      forAll(Gen.choose(1, 1000000)):
        bytes =>
          val result = controller.randomResponseBodyOfSize()(FakeRequest(GET, s"/?bytes=$bytes&headerName=").withCSRFToken)

          status(result) shouldBe Status.OK
          contentAsBytes(result).length shouldBe bytes


  "GET /measure-header-backend" should:
    "call the backend with a random byte filled header of the specified name and size" in new Setup():
      forAll(Gen.choose(1, 1000000), nonEmptyString):
        (bytes, headerName) =>
          reset(backendConnector) //Required to reset so mock verify works when called repeatedly from scalacheck

          val captor = ArgumentCaptor.forClass(classOf[Seq[(String, String)]])
          when(backendConnector.measure(any, captor.capture())(using any)).thenReturn(Future.successful(s"response from backend"))

          val result = controller.headerOfSizeToBackend()(FakeRequest(GET, s"/?bytes=$bytes&headerName=$headerName").withCSRFToken)
          verify(backendConnector, times(1)).measure(any, any)(using any)

          status(result) shouldBe Status.OK
          captor.getValue.toMap.get(headerName).map(byteSize) shouldBe Some(bytes)

  "GET /measure-body-backend" should:
    "call the backend with a random byte filled body of the specified size" in new Setup():
      forAll(Gen.choose(1, 1000000)):
        bytes =>
          reset(backendConnector)

          val captor = ArgumentCaptor.forClass(classOf[String])
          when(backendConnector.measure(captor.capture(), any)(using any)).thenReturn(Future.successful(s"response from backend"))

          val result = controller.bodyOfSizeToBackend()(FakeRequest(GET, s"/?bytes=$bytes&headerName=").withCSRFToken)
          verify(backendConnector, times(1)).measure(any, any)(using any)

          status(result) shouldBe Status.OK
          byteSize(captor.getValue) shouldBe bytes
