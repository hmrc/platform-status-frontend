/*
 * Copyright 2025 HM Revenue & Customs
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

import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment, Logger}
import uk.gov.hmrc.platformstatusfrontend.config.AppConfig

import javax.inject.Inject

class DynatraceModule extends Module:
  override def bindings(
                         environment: Environment,
                         configuration: Configuration
                       ): Seq[Binding[_]] =
    Seq(
      bind[DynatraceInfo].to[DefaultDynatraceInfo].eagerly()
    )

trait DynatraceInfo

class DefaultDynatraceInfo @Inject()(config: AppConfig) extends DynatraceInfo:

  private val logger = Logger(this.getClass)

  logger.info(s"DT_TENANT: ${sys.env.getOrElse("DT_TENANT", "not set")}")
  logger.info(s"DT_CONNECTION_POINT: ${sys.env.getOrElse("DT_CONNECTION_POINT", "not set")}")
  logger.info(s"DT_TAGS: ${sys.env.getOrElse("DT_TAGS", "not set")}")
  logger.info(s"DT_CUSTOM_PROP: ${sys.env.getOrElse("DT_CUSTOM_PROP", "not set")}")
  