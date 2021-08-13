/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.platformstatusfrontend.guice

import org.mockito.Mockito._
import org.scalatest.concurrent.TimeLimits
import org.scalatest.exceptions.TestFailedDueToTimeoutException
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.time.Span
import org.scalatest.{Matchers, WordSpec}
import uk.gov.hmrc.platformstatusfrontend.config.AppConfig

import scala.concurrent.duration._


class SlowStartModuleSpec extends WordSpec with Matchers with MockitoSugar with TimeLimits {

  private trait Setup {
    val testTimeoutDuration: Span = 100 milliseconds

    val appConfig = mock[AppConfig]
  }

  "DefaultSlowStarter" should {
    "wait n time before startup" in new Setup() {
      when(appConfig.startupDelay) thenReturn (Some(5000))

      intercept[TestFailedDueToTimeoutException] {
        failAfter(testTimeoutDuration) {
          new DefaultSlowStarter(appConfig)
        }
      }
    }
  }

}
