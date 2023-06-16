package uk.gov.hmrc.platformstatusfrontend.filters

import akka.stream.Materializer
import play.api.mvc.Call
import uk.gov.hmrc.allowlist.AkamaiAllowlistFilter

import javax.inject.Inject

class TestFilterDeleteMe @Inject() (materializer: Materializer) extends AkamaiAllowlistFilter{
  override def allowlist: Seq[String] = Seq.empty

  override def destination: Call = Call("GET", "http://www.gov.uk")

  override implicit def mat: Materializer = materializer
}
