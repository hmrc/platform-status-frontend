/*
 * Copyright 2026 HM Revenue & Customs
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

import play.api.mvc.*
import uk.gov.hmrc.platformstatusfrontend.auth.PlatformStatusAuthAction

import javax.inject.{Inject, Singleton}

@Singleton
class AuthenticatedPlatformStatusController @Inject()(
  authAction             : PlatformStatusAuthAction,
  mcc                    : MessagesControllerComponents,
  statusController       : StatusController,
  noiseController        : NoiseController,
  killController         : KillController,
  garbageController      : GarbageController,
  codeController         : CodeController,
  measureController      : MeasureController,
  auditVolumeController  : AuditVolumeController,
  serviceVolumeController: ServiceVolumeController
):

  def defaultLanding: Action[AnyContent] =
    authenticated(statusController.defaultLanding)

  def platformStatus: Action[AnyContent] =
    authenticated(statusController.platformStatus)

  def noise: Action[AnyContent] =
    authenticated(noiseController.noise)

  def createNoise: Action[AnyContent] =
    authenticated(noiseController.createNoise)

  def kill: Action[AnyContent] =
    authenticated(killController.kill)

  def meteOutDeath: Action[AnyContent] =
    authenticated(killController.meteOutDeath)

  def leakMemory: Action[AnyContent] =
    authenticated(killController.leakMemory)

  def garbage: Action[AnyContent] =
    authenticated(garbageController.garbage)

  def code: Action[Unit] =
    authenticated(mcc.parsers.empty)(codeController.code)

  def respondWithCode: Action[Map[String, Seq[String]]] =
    authenticated(mcc.parsers.formUrlEncoded)(codeController.respondWithCode)

  def measure: Action[AnyContent] =
    authenticated(measureController.measure)

  def measureHeader: Action[AnyContent] =
    authenticated(measureController.measureHeader)

  def measureBody: Action[AnyContent] =
    authenticated(measureController.measureBody)

  def randomResponseHeaderOfSize: Action[AnyContent] =
    authenticated(measureController.randomResponseHeaderOfSize)

  def randomResponseBodyOfSize: Action[AnyContent] =
    authenticated(measureController.randomResponseBodyOfSize)

  def headerOfSizeToBackend: Action[AnyContent] =
    authenticated(measureController.headerOfSizeToBackend)

  def bodyOfSizeToBackend: Action[AnyContent] =
    authenticated(measureController.bodyOfSizeToBackend)

  def auditVolumeSetup: Action[AnyContent] =
    authenticated(auditVolumeController.setup)

  def auditVolumeRun: Action[AnyContent] =
    authenticated(auditVolumeController.run)

  def serviceVolumeSetup: Action[AnyContent] =
    authenticated(serviceVolumeController.setup)

  def serviceVolumeRun: Action[AnyContent] =
    authenticated(serviceVolumeController.run)

  private def authenticated(delegate: Action[AnyContent]): Action[AnyContent] =
    authAction.async: request =>
      delegate(request)

  private def authenticated[A](parser: BodyParser[A])(delegate: Action[A]): Action[A] =
    authAction.async(parser): request =>
      delegate(request)
