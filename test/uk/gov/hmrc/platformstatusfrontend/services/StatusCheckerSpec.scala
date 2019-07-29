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
import org.mongodb.scala.MongoSocketException
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.mockito.MockitoSugar
import play.api.libs.concurrent.{DefaultFutures, Futures}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.platformstatusfrontend.connectors.BackendConnector
import org.mockito.ArgumentMatchers._
import org.scalatest.time.Span

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class StatusCheckerSpec extends WordSpec with Matchers with MockitoSugar with ScalaFutures {

  private val testTimeoutDuration: Span = 6 seconds // underlying method calls should timeout before this

  private trait Setup {
    implicit val headerCarrier: HeaderCarrier = HeaderCarrier()
    val backendConnectorMock = mock[BackendConnector]
    implicit val futures: Futures = new DefaultFutures(ActorSystem.create)
  }

  "iteration 2 status checker" should {
    "connect to Mongo" in new Setup() {
      whenReady(new StatusChecker(backendConnectorMock).iteration2Status("mongodb://localhost:27017"), timeout(testTimeoutDuration)) {
        r =>
          r shouldBe PlatformStatus.baseIteration2Status
          r.isWorking shouldBe true
      }
    }
    "fail to connect to Mongo" in new Setup() {
      whenReady(new StatusChecker(backendConnectorMock).iteration2Status("mongodb://not_there:27017"), timeout(testTimeoutDuration)) {
        r => r shouldBe PlatformStatus.baseIteration2Status.copy(isWorking = false, reason = Some("Timeout after 2 seconds"))
          r.isWorking shouldBe false
      }
    }
  }
  // TODO - iteration3 tests

}
