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

package uk.gov.hmrc.platformstatusfrontend.services

import com.google.inject.Inject
import com.mongodb.client.model.UpdateOptions
import javax.inject.Singleton
import org.mongodb.scala._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.{ReplaceOptions, UpdateOptions}
import play.api.Logger
import play.api.libs.concurrent.{Futures, Timeout}
import play.api.libs.concurrent.Futures._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.platformstatusfrontend.connectors.BackendConnector

import scala.concurrent.duration._
import PlatformStatus._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class StatusChecker @Inject()(backendConnector: BackendConnector) extends Timeout {



  def iteration1Status(): PlatformStatus = baseIteration1Status

  def iteration2Status(dbUrl: String)(implicit executionContext: ExecutionContext, futures: Futures): Future[PlatformStatus] = {
    checkMongoConnection(dbUrl).withTimeout(2.seconds).recoverWith {
      case ex: Exception => {
        Logger.warn("Failed to connect to Mongo")
        Future(baseIteration2Status.copy(isWorking = false, reason = Some(ex.getMessage)))
      }
    }
  }

  private def checkMongoConnection(dbUrl: String)(implicit executionContext: ExecutionContext, futures: Futures): Future[PlatformStatus] = {
    val mongoClient: MongoClient = MongoClient(dbUrl)
    val database: MongoDatabase = mongoClient.getDatabase("platform-status-frontend")
    val collection: MongoCollection[Document] = database.getCollection("status");
    val doc: Document = Document("_id" -> 0, "name" -> "MongoDB")

    for {
      _ <- collection.replaceOne(equal(fieldName = "_id", value = 0), doc, ReplaceOptions().upsert(true)).toFuture()
      result = baseIteration2Status
      // TODO - handle error states better
    } yield result
  }

  def iteration3Status()(implicit headerCarrier: HeaderCarrier): Future[PlatformStatus] = backendConnector.iteration3Status()

  def iteration4Status() = baseIteration4Status.copy(isWorking = false, reason = Some("Test not yet implemented"))
  def iteration5Status() = baseIteration5Status.copy(isWorking = false, reason = Some("Test not yet implemented"))



}
