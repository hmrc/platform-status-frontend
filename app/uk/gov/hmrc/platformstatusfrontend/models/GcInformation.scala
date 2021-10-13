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

package uk.gov.hmrc.platformstatusfrontend.models

import play.api.libs.json.{Json, OFormat}

import java.lang.management.GarbageCollectorMXBean

case class GcInformation(coreCount: Int, gBeans: Seq[GcBeanInfo])


object GcInformation {
  def apply(coreCount: Int, beans: Iterable[GarbageCollectorMXBean]): GcInformation = {
    new GcInformation(coreCount, beans.map(GcBeanInfo(_)).toSeq)
  }
  implicit val formatter: OFormat[GcInformation] = Json.format[GcInformation]
}

case class GcBeanInfo(name: String, collectionCount: Long)

object GcBeanInfo {
  def apply(bean: GarbageCollectorMXBean): GcBeanInfo = {
    new GcBeanInfo(bean.getName, bean.getCollectionCount)
  }
  implicit val formatter: OFormat[GcBeanInfo] = Json.format[GcBeanInfo]
}

case class GcSummary(frontend: GcInformation, backend: GcInformation)