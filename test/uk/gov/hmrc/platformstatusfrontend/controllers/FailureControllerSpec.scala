package uk.gov.hmrc.platformstatusfrontend.controllers

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.{JsNull, JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

class FailureControllerSpec extends WordSpec with Matchers with MockitoSugar  {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()


  import scala.concurrent.ExecutionContext.Implicits.global
  val controller = new FailureController(stubMessagesControllerComponents())

  val request = FakeRequest("POST", "/platform-status/fail").withHeaders( "Content-Type" -> "application/json")

  "POST without a body" should {
    "return a actual bad request" in {
      val result = controller.fail(request.withBody[JsValue](JsNull))
      status(result) should be (400)
      contentAsString(result) should startWith ("Bad Request, this is not the error you asked for, something actually went wrong")
    }
  }

  "POST with a valid body" should {

    val json = Json.parse("""{"status_code": 500, "msg":"test123", "delay_in_ms":0}""")

    "return the requested status code" in {
      val r = request.withBody[JsValue](json)
      val result = controller.fail(r)
      status(result) should be (500)
    }

    "return the requested message" in {
      val r = request.withBody[JsValue](json)
      val result = controller.fail(r)
      contentAsString(result) should be ("test123")
    }
  }

  "POST without a delay-in-ms" should {

    val json = Json.parse("""{"status_code": 500, "msg":"test123"}""")

    "not result in a bad request" in {
      val r = request.withBody[JsValue](json)
      val result = controller.fail(r)
      status(result) should be (500)
    }
  }
}
