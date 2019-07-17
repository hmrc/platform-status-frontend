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

import org.mongodb.scala.MongoSocketException
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.mockito.MockitoSugar

class StatusControllerSpec extends WordSpec with Matchers with MockitoSugar {

  "iteration 2 status checker" should {
    "connect to Mongo" in {
      new StatusChecker().iteration2Status("mongodb://localhost:27017").isWorking shouldBe true
    }
    "fail to connect to Mongo" in {
        new StatusChecker().iteration2Status("mongodb://not_there:27017").isWorking shouldBe false
    }
  }

}
