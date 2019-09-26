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

import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, MessagesControllerComponents}

import uk.gov.hmrc.play.bootstrap.controller.BackendController
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.concurrent.{ExecutionContext, Future}


case class FailureRequest(statusCode: Int, delayInMs: Long = 0, msg: String)


@Singleton
class FailureController @Inject()(mcc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends BackendController(mcc) {

  implicit val failureRequestReads: Reads[FailureRequest] = (
    (__ \ "status_code" ).read[Int] ~
    (__ \ "delay_in_ms" ).readWithDefault[Long](defaultValue = 0) ~
    (__ \ "msg"         ).read[String]
  )(FailureRequest.apply _)

  def fail: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[FailureRequest].fold(
      err => {
        val errorMsg = err.foldLeft(new StringBuilder("Bad Request, this is not the error you asked for, something actually went wrong:\n"))(
          (sb,e) => sb.append(e._1.toString())
                      .append(" - ")
                      .append(e._2.flatMap(_.messages))
                      .append("\n")).toString()
        Future.successful(BadRequest(errorMsg))
      },
      failureRequest => Future {
        Thread.sleep(failureRequest.delayInMs)
        Status(failureRequest.statusCode)(failureRequest.msg)
      })
  }

}