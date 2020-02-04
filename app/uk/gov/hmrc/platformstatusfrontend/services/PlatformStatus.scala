/*
 * Copyright 2020 HM Revenue & Customs
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

import play.api.libs.json.{Json, OFormat}

case class PlatformStatus(name: String,
                          isWorking: Boolean,
                          description: String,
                          reason: Option[String] = None) {

}

object PlatformStatus {
  implicit val mongoFormat: OFormat[PlatformStatus] = Json.format[PlatformStatus]

  val baseIteration1Status = PlatformStatus(name = "iteration 1",
    isWorking = true,
    description = "Service up and running in the public zone.")

  val baseIteration2Status = PlatformStatus(name = "iteration 2",
    isWorking = true,
    description = "Read and write to Mongo in public zone")

  val baseIteration3Status = PlatformStatus(name = "iteration 3",
    isWorking = true,
    description = "Call through to service in protected zone that can read/write to protected Mongo")

  val baseIteration4Status = PlatformStatus(name = "iteration 4",
    isWorking = true,
    description = "Call out to internet via squid from a service in the public zone")

  val baseIteration5Status = PlatformStatus(name = "iteration 5",
    isWorking = true,
    description = "Call through to service in protected zone that can call a HOD via DES")


}
