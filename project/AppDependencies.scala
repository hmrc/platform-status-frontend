import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

val bootstrapVersion = "7.15.0"

  val compile = Seq(

    "uk.gov.hmrc"             %% "play-frontend-hmrc"          % "7.3.0-play-28",
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-28"  % bootstrapVersion,
    "org.mongodb.scala"       %% "mongo-scala-driver"          % "4.8.2",
    "org.typelevel"           %% "cats-core"                   % "2.6.1"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"   % bootstrapVersion      % "test, it",
    "org.scalatestplus"       %% "scalacheck-1-14"          % "3.1.1.0"             % "test, it",
    "org.jsoup"               %  "jsoup"                    % "1.14.3"              % Test,
    "org.mockito"             %% "mockito-scala-scalatest"  % "1.16.42"             % Test,
  )
}
