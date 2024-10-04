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
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import scala.concurrent.ExecutionContext.Implicits.global

class AuditVolumeServiceSpec
  extends AnyWordSpec
     with Matchers
     with MockitoSugar
     with BeforeAndAfterEach {

  val auditConnector: AuditConnector = mock[AuditConnector]
  val fixture = new AuditVolumeService(auditConnector)
  implicit val hc: HeaderCarrier = HeaderCarrier()

  override def beforeEach(): Unit = {
    reset(auditConnector)
  }

  "sendAuditMessages" should {
    "send a specified number of messages" in {
      fixture.sendAuditMessages("test", 3)

      verify(auditConnector).sendExplicitAudit("test", Map("messageNo" -> "1"))
      verify(auditConnector).sendExplicitAudit("test", Map("messageNo" -> "2"))
      verify(auditConnector).sendExplicitAudit("test", Map("messageNo" -> "3"))
    }
  }
}
