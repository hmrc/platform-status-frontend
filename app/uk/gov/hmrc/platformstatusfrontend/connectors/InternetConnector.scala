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

package uk.gov.hmrc.platformstatusfrontend.connectors

import com.google.inject.Inject
import javax.inject.Singleton
import play.api.Logger
import play.api.libs.ws.{DefaultWSProxyServer, WSClient}
import uk.gov.hmrc.platformstatusfrontend.config.AppConfig

@Singleton
class InternetConnector @Inject()(config: AppConfig, wsClient: WSClient) {

  val logger = Logger(this.getClass)

  lazy val proxyServer = DefaultWSProxyServer(
    protocol = Some(config.proxyProtocol),
    host = config.proxyHost,
    port = config.proxyPort,
    principal = Some(config.proxyUsername),
    password = Some(config.proxyPassword))

  def callTheWeb(url: String, proxyRequired: Boolean)= {
    proxyRequired match {
      case true => wsClient.url(url).withProxyServer(proxyServer).get()
      case _ => {
        logger.info("Proxy configured off.  Attempting to access internet directly. Consider setting the proxy.required config setting.")
        wsClient.url(url).get()
      }
    }

  }




}
