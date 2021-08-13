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

package uk.gov.hmrc.platformstatusfrontend.util

import org.scalacheck.Gen
import org.scalacheck.Gen.{alphaChar, chooseNum, listOfN}

object Generators {

  val strGen: Int => Gen[String] = (n: Int) => listOfN(n, alphaChar).map(_.mkString)
  val nonEmptyString: Gen[String] = chooseNum(1, 15).flatMap(n => strGen(n))

  val headersGen: Gen[Seq[(String, String)]] = for {
    numHeaders <- chooseNum(0, 100)
    h <- listOfN(numHeaders, nonEmptyString)
    v <- listOfN(numHeaders, nonEmptyString)
  } yield h zip v

}
