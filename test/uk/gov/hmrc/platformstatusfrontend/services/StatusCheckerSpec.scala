/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.platformstatusfrontend.services

import akka.actor.ActorSystem
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.mockito.MockitoSugar
import play.api.libs.concurrent.{DefaultFutures, Futures}
import uk.gov.hmrc.http.{HeaderCarrier, Upstream5xxResponse}
import uk.gov.hmrc.platformstatusfrontend.connectors.{BackendConnector, InternetConnector}
import org.mockito.ArgumentMatchers._
import org.scalatest.time.Span
import PlatformStatus._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import org.mockito.Mockito._
import play.api.Configuration
import play.api.libs.ws.WSResponse
import uk.gov.hmrc.platformstatusfrontend.config.AppConfig

class StatusCheckerSpec extends WordSpec with Matchers with MockitoSugar with ScalaFutures {

  private val testTimeoutDuration: Span = 6 seconds // underlying method calls should timeout before this

  private trait Setup {
    implicit val headerCarrier: HeaderCarrier = HeaderCarrier()
    val backendConnectorMock = mock[BackendConnector]
    val internetConnector = mock[InternetConnector]
    implicit val futures: Futures = new DefaultFutures(ActorSystem.create)
    val appConfig = mock[AppConfig]
    val statusChecker = new StatusChecker(backendConnectorMock, internetConnector, appConfig)
  }

  "iteration 2 status checker" should {
    "connect to Mongo" in new Setup() {
      when(appConfig.dbUrl) thenReturn ("mongodb://localhost:27017")
      whenReady(statusChecker.iteration2Status(), timeout(testTimeoutDuration)) {
        r =>
          r shouldBe baseIteration2Status
          r.isWorking shouldBe true
      }
    }
    "fail to connect to Mongo" in new Setup() {
      when(appConfig.dbUrl) thenReturn ("mongodb://not_there:27017")
      whenReady(statusChecker.iteration2Status(), timeout(testTimeoutDuration)) {
        r => r shouldBe baseIteration2Status.copy(isWorking = false, reason = Some("Timeout after 2 seconds"))
          r.isWorking shouldBe false
      }
    }
  }
  "iteration 3 status check" should {
    "be happy when backend responds with a good result" in new Setup() {
      when(backendConnectorMock.iteration3Status()) thenReturn Future(baseIteration3Status)
      whenReady(statusChecker.iteration3Status(), timeout(testTimeoutDuration) ){
        result => result shouldBe baseIteration3Status
      }
    }
    "not blow up when backend responds with a bad result" in new Setup() {
      when(backendConnectorMock.iteration3Status()) thenReturn  Future.failed(Upstream5xxResponse("Borked", 500, 500))
      whenReady(statusChecker.iteration3Status(), timeout(testTimeoutDuration) ){
        result => result shouldBe baseIteration3Status.copy(isWorking = false, reason = Some("Borked"))
      }
    }
  }
  "iteration 4 outbound call via squid" should {
    "be happy when a response is received" in new Setup() {
      val fakeResponse = mock[WSResponse]
      when(internetConnector.callTheWeb(statusChecker.webTestEndpoint, false)) thenReturn Future(fakeResponse)
      whenReady(statusChecker.iteration4Status(), timeout(testTimeoutDuration)) {
        result => result shouldBe baseIteration4Status
      }
    }
    "handle things when an error response is received" in new Setup() {
      val fakeResponse = mock[WSResponse]
      when(internetConnector.callTheWeb(statusChecker.webTestEndpoint, false)) thenReturn Future.failed(new Exception("Borked"))
      whenReady(statusChecker.iteration4Status(), timeout(testTimeoutDuration)) {
        result => result shouldBe baseIteration4Status.copy(isWorking = false, reason = Some("Borked"))
      }
    }
  }

}
