package uk.gov.hmrc.platformstatusfrontend.services

import org.mockito.scalatest.MockitoSugar
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.platformstatusfrontend.config.AppConfig
import uk.gov.hmrc.platformstatusfrontend.connectors.BackendConnector
import uk.gov.hmrc.platformstatusfrontend.models.{GcBeanInfo, GcInformation}

import scala.concurrent.Future

class GarbageServiceSpec extends AsyncWordSpec with Matchers with MockitoSugar with BeforeAndAfterEach {

    implicit val headerCarrier: HeaderCarrier = HeaderCarrier()
    val backendConnectorMock = mock[BackendConnector]
    val appConfig = mock[AppConfig]
    val garbageService = new GarbageService(backendConnectorMock, appConfig)

  override def beforeEach = {
    reset(backendConnectorMock)
  }

  "getBackendGcInfo" should {
    "return Gc Info" in {
      when(backendConnectorMock.gcInformation()(any)) thenReturn Future.successful(GcInformation(1, Seq[GcBeanInfo]()))
      val result = garbageService.getBackendGcInfo
      result map { info => info.coreCount shouldBe 1}
    }
    "return core count -1 when backend call fails" in {
      when(backendConnectorMock.gcInformation()(any)) thenReturn Future.failed(new Exception(""))
      val result = garbageService.getBackendGcInfo
      result map { info => info.coreCount shouldBe -1}
    }
  }
  "getFrontendGcInfo" should {
    "Return a coreCount > 0" in {
      val result = garbageService.getFrontendGcInfo
      result map { info => info.coreCount should be > 0}
    }
  }
}
