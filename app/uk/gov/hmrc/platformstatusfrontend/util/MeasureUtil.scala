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

package uk.gov.hmrc.platformstatusfrontend.util

import java.nio.charset.StandardCharsets

import scala.util.Random

object MeasureUtil {

  def byteSize(s: String): Int = s.getBytes(StandardCharsets.UTF_8).length

  def generateStringOfSize(length: Int): String = {
    def r: Char = Random.alphanumeric.filter(_.isLetter).head
    (1 to length).map(_ => r).mkString
  }

}
