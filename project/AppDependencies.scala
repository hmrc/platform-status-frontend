import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(

    "uk.gov.hmrc"             %% "govuk-template"              % "5.71.0-play-28",
    "uk.gov.hmrc"             %% "play-ui"                     % "9.7.0-play-28",
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-28"  % "5.14.0",
    "org.mongodb.scala"       %% "mongo-scala-driver"          % "2.6.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"   % "5.14.0"              % Test,
    "org.scalatestplus"       %% "scalacheck-1-14"          % "3.1.1.0"             % Test,
    "org.jsoup"               %  "jsoup"                    % "1.14.3"              % Test,
    "org.mockito"             %% "mockito-scala-scalatest"  % "1.16.42"             % Test,
  )

}
