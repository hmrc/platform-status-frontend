import sbt.*

object AppDependencies {

  val bootstrapVersion = "10.7.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30"   % "12.32.1",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30"   % bootstrapVersion,
    "uk.gov.hmrc"       %% "internal-auth-client-play-30" % "4.4.0",
    "org.typelevel"     %% "cats-core"                    % "2.13.0",
    "org.mongodb.scala" %% "mongo-scala-driver"           % "5.5.1" cross CrossVersion.for3Use2_13
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-test-play-30"  % bootstrapVersion % Test,
    "org.jsoup"         %  "jsoup"                   % "1.18.1"         % Test,
    "org.scalatestplus" %% "scalacheck-1-18"         % "3.2.19.0"       % Test
  )
}
