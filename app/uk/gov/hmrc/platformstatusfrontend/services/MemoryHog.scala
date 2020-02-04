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

import javax.inject.Singleton
import play.api.Logger

import scala.concurrent.Future
import scala.util.Random

@Singleton
class MemoryHog (){

  val logger = Logger(this.getClass)
  val megabyte = 1024 * 1024

  def eatMemory(mbs: Int, sleep: Int) = {

    var bigArray = Array[Array[Byte]]()

    while (true) {
      bigArray = bigArray :+ Array.fill[Byte](mbs * megabyte)('a')
      logger.info(s"Leaking memory! - Max memory:${Runtime.getRuntime.maxMemory() / megabyte} - Free memory:${Runtime.getRuntime().freeMemory() / megabyte}")
      Thread.sleep(sleep)
    }
  }

}
