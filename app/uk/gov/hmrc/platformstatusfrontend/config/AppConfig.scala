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

package uk.gov.hmrc.platformstatusfrontend.config

import play.api.Configuration

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.{Duration, FiniteDuration}

@Singleton
class AppConfig @Inject()(config: Configuration) {

  val analyticsToken: String      = config.get[String](s"google-analytics.token")
  val analyticsHost : String      = config.get[String](s"google-analytics.host")
  val startupDelay  : Option[Int] = config.getOptional[Int]("startup-delay")

  lazy val dbUrl                  = config.get[String]("mongodb.uri")

  lazy val proxyProtocol: String   = config.get[String]("proxy.protocol")
  lazy val proxyHost    : String   = config.get[String]("proxy.host")
  lazy val proxyPort    : Integer  = config.get[Int]("proxy.port")
  lazy val proxyTimeout : FiniteDuration = config.get[FiniteDuration]("proxy.timeout")
  lazy val proxyUsername: String   = config.get[String]("proxy.username")
  lazy val proxyPassword: String   = config.get[String]("proxy.password")
  lazy val proxyRequired: Boolean  = config.get[Boolean]("proxy.required")

  lazy val badGatewayTimeout: Duration = config.get[Duration]("bad-gateway.timeout")

  lazy val experimentValue: String = config.get[String]("experiment.value")
}
