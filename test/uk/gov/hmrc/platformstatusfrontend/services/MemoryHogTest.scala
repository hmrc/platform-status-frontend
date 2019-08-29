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

import org.scalatest.{FunSuite, Matchers, WordSpec}

class MemoryHogTest extends WordSpec with Matchers {

  "MemoryHog" should {
    "eat memory" in {
      try {
        val memHog = new MemoryHog()
        memHog.eatMemory(50, 25)
      } catch {
        case _: OutOfMemoryError =>
        case ex => throw ex
      }
    }
  }

}
