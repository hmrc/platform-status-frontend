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

import org.mockito.Mockito.{reset, verify}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.platformstatusfrontend.connectors.GenericConnector

class ServiceVolumeServiceSpec
  extends AnyWordSpec
     with Matchers
     with MockitoSugar
     with BeforeAndAfterEach {

  val connector = mock[GenericConnector]
  val fixture = new ServiceVolumeService(connector)
  implicit val hc: HeaderCarrier = HeaderCarrier()

  override def beforeEach(): Unit = {
    reset(connector)
  }

  "sendServiceCalls" should {
    "send a specified number of messages" in {
      fixture.sendServiceCalls("http://localhost:9000/", 3)

      verify(connector).callWebService(url"http://localhost:9000/?messageNo=1");
      verify(connector).callWebService(url"http://localhost:9000/?messageNo=2");
      verify(connector).callWebService(url"http://localhost:9000/?messageNo=3");
    }
  }
}
