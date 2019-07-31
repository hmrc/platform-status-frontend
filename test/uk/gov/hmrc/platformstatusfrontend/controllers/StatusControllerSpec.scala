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

package uk.gov.hmrc.platformstatusfrontend.controllers

import akka.actor.ActorSystem
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.concurrent.Futures
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Configuration, Environment, _}
import play.api.libs.concurrent.DefaultFutures
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.platformstatusfrontend.config.AppConfig
import uk.gov.hmrc.platformstatusfrontend.services.{PlatformStatus, StatusChecker}
import uk.gov.hmrc.play.bootstrap.config.{RunMode, ServicesConfig}
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class StatusControllerSpec extends WordSpec with Matchers with GuiceOneAppPerSuite with MockitoSugar {
  private val fakeRequest = FakeRequest("GET", "/")
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

    val statusChecker = mock[StatusChecker]
    val dummyStatus = PlatformStatus("name", true, "description", Some("No reason"))
    when(statusChecker.iteration1Status()) thenReturn dummyStatus.copy(name = "it1")
    when(statusChecker.iteration2Status(anyString()) (any[ExecutionContext], any[Futures]) ) thenReturn Future(dummyStatus.copy(name = "it2"))
    when(statusChecker.iteration3Status()(any[HeaderCarrier], any[ExecutionContext]  )) thenReturn Future(dummyStatus.copy(name = "it3"))
    when(statusChecker.iteration4Status()(any[ExecutionContext], any[Futures]  ) ) thenReturn Future(dummyStatus.copy(name = "it4"))
    when(statusChecker.iteration5Status()) thenReturn dummyStatus.copy(name = "it5")

    val controller = new StatusController(appConfig, stubMessagesControllerComponents(), statusChecker)
  }

  "GET /" should {
    "return 200" in new Setup() {

      val result = controller.platformStatus(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in new Setup() {
      val result = controller.platformStatus(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

  }
}
