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

package uk.gov.hmrc.platformstatusfrontend.controllers

import akka.actor.ActorSystem
import org.scalacheck.Gen
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.concurrent.{DefaultFutures, Futures}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Configuration, Environment, _}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.platformstatusfrontend.config.AppConfig
import uk.gov.hmrc.platformstatusfrontend.services.MeasureService
import uk.gov.hmrc.play.bootstrap.config.{RunMode, ServicesConfig}
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents
import scala.concurrent.ExecutionContext.Implicits.global

class MeasureControllerSpec extends WordSpec with Matchers with GuiceOneAppPerSuite with MockitoSugar with ScalaCheckDrivenPropertyChecks with ScalaFutures {
  private val env           = Environment.simple()
  private val configuration: Configuration = Configuration.load(env)
  private val serviceConfig = new ServicesConfig(configuration, new RunMode(configuration, Mode.Dev))
  private val appConfig     = new AppConfig(configuration, serviceConfig)

  override def fakeApplication: Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm" -> false
      )
      .build()

  private trait Setup {
    implicit val headerCarrier: HeaderCarrier = HeaderCarrier()
    implicit val futures: Futures = new DefaultFutures(ActorSystem.create)

    val measureService = mock[MeasureService]
    val controller = new MeasureController(appConfig, stubMessagesControllerComponents(), measureService)
  }

  "GET /measure-body-size" should {
    "return 200" in new Setup() {
      forAll(Gen.choose(1, 1000000)){ length =>
        val result = controller.randomBodyOfSize(length)(FakeRequest("GET", s"/?length=$length"))

        status(result) shouldBe Status.OK
        contentAsBytes(result).length shouldBe length
      }
    }

  }
}
