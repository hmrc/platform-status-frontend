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

package uk.gov.hmrc.platformstatusfrontend.guice

import javax.inject.Inject
import play.api.{Configuration, Environment, Logger}
import play.api.inject.{Binding, Module}
import uk.gov.hmrc.platformstatusfrontend.config.AppConfig

class SlowStartModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
    bind[SlowStarter].to[DefaultSlowStarter].eagerly()
  )
}

trait SlowStarter {}

class DefaultSlowStarter @Inject()(config: AppConfig) extends SlowStarter {

  private val logger = Logger(this.getClass)

  config.startupDelay foreach { delay =>
    logger.info(s"Delaying application startup by $delay milliseconds, as specified by 'startup-delay' configuration")
    Thread.sleep(delay)
  }
}
