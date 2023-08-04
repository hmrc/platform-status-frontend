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

package uk.gov.hmrc.platformstatusfrontend.services

import com.google.inject.Inject
import org.mongodb.scala._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.ReplaceOptions
import play.api.Logger
import play.api.libs.concurrent.Futures
import play.api.libs.concurrent.Futures._
import play.api.libs.ws.WSResponse
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.platformstatusfrontend.config.AppConfig
import uk.gov.hmrc.platformstatusfrontend.connectors.{BackendConnector, InternetConnector}
import uk.gov.hmrc.platformstatusfrontend.services.PlatformStatus._

import javax.inject.Singleton
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StatusChecker @Inject()(
  backendConnector : BackendConnector,
  internetConnector: InternetConnector,
  appConfig        : AppConfig
)(implicit
  ec     : ExecutionContext,
  futures: Futures
) {
  val logger: Logger = Logger(this.getClass)

  val webTestEndpoint = "https://www.gov.uk/bank-holidays.json"

  def iteration1Status(): Future[PlatformStatus] =
    if(appConfig.iteration1Enabled)
      Future.successful(baseIteration1Status)
    else
      Future.successful(baseIteration1Status.copy(enabled = false))

  def iteration2Status(): Future[PlatformStatus] =
    if(appConfig.iteration2Enabled) {
      try {
        checkMongoConnection(appConfig.dbUrl)
          .withTimeout(2.seconds)
          .recoverWith {
            case ex: Exception =>
              logger.warn("Failed to connect to Mongo")
              genericError(baseIteration2Status, ex)
          }
      } catch {
        case ex: Exception => genericError(baseIteration2Status, ex)
      }
    } else Future.successful(baseIteration2Status.copy(enabled = false))

  private def checkMongoConnection(dbUrl: String): Future[PlatformStatus] = {
    val mongoClient: MongoClient               = MongoClient(dbUrl)
    val database   : MongoDatabase             = mongoClient.getDatabase("platform-status-frontend")
    val collection : MongoCollection[Document] = database.getCollection("status")
    val doc        : Document                  = Document("_id" -> 0, "name" -> "MongoDB")

    for {
      _      <- collection
                  .replaceOne(equal(fieldName = "_id", value = 0), doc, ReplaceOptions().upsert(true))
                  .toFuture()
      result =  baseIteration2Status
      // TODO - handle error states better
    } yield result
  }

  def iteration3Status()(implicit hc: HeaderCarrier): Future[PlatformStatus] = {
    if(appConfig.iteration3Enabled) {
      backendConnector.iteration3Status().recoverWith {
        case ex: Exception =>
          logger.warn("iteration3Status call to backend service failed.")
          genericError(baseIteration3Status, ex)
      }
    } else Future.successful(baseIteration3Status.copy(enabled = false))
  }

  def iteration4Status(): Future[PlatformStatus] =
    if(appConfig.iteration4Enabled) {
      for {
        wsResult <- internetConnector
          .callTheWeb(webTestEndpoint, appConfig.proxyRequired)
          .withTimeout(appConfig.proxyTimeout)
          .recoverWith {
            case ex: Exception =>
              logger.warn("Unable to call out via squid proxy")
              genericError(baseIteration4Status, ex)
          }
      } yield
        wsResult match {
          case r: WSResponse if r.status < 300 => baseIteration4Status
          case e: PlatformStatus => e
          case _ => throw new IllegalStateException("That shouldn't happen")
        }
    } else Future.successful(baseIteration4Status.copy(enabled = false))

  def iteration5Status()(implicit hc: HeaderCarrier): Future[PlatformStatus] =
    if(appConfig.iteration5Enabled) {
      backendConnector.iteration5Status()
        .recoverWith {
          case ex: Exception =>
            logger.warn("iteration5Status call to backend service failed.")
            genericError(baseIteration5Status, ex)
        }
    } else Future.successful(baseIteration5Status.copy(enabled = false))

  private def genericError(status: PlatformStatus, ex: Exception): Future[PlatformStatus] =
    Future.successful(status.copy(isWorking = false, reason = Some(ex.getMessage)))
}
