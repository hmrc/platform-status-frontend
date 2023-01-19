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

package uk.gov.hmrc.platformstatusfrontend.services

import org.mockito.scalatest.MockitoSugar
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.platformstatusfrontend.config.AppConfig
import uk.gov.hmrc.platformstatusfrontend.connectors.BackendConnector
import uk.gov.hmrc.platformstatusfrontend.models.{GcBeanInfo, GcInformation}

import scala.concurrent.Future

class GarbageServiceSpec extends AsyncWordSpec with Matchers with MockitoSugar with BeforeAndAfterEach {

    implicit val headerCarrier: HeaderCarrier = HeaderCarrier()
    val backendConnectorMock = mock[BackendConnector]
    val appConfig = mock[AppConfig]
    val garbageService = new GarbageService(backendConnectorMock, appConfig)

  override def beforeEach = {
    reset(backendConnectorMock)
  }

  "getBackendGcInfo" should {
    "return Gc Info" in {
      when(backendConnectorMock.gcInformation()(any)) thenReturn Future.successful(GcInformation(1, Seq[GcBeanInfo]()))
      val result = garbageService.getBackendGcInfo
      result map { info => info.coreCount shouldBe 1}
    }
    "return core count -1 when backend call fails" in {
      when(backendConnectorMock.gcInformation()(any)) thenReturn Future.failed(new Exception(""))
      val result = garbageService.getBackendGcInfo
      result map { info => info.coreCount shouldBe -1}
    }
  }
  "getFrontendGcInfo" should {
    "Return a coreCount > 0" in {
      val result = garbageService.getFrontendGcInfo
      result map { info => info.coreCount should be > 0}
    }
  }
}
