import sbt.*

object AppDependencies {

  val bootstrapVersion = "9.5.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30" % "10.12.0",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30" % bootstrapVersion,
    "org.typelevel"     %% "cats-core"                  % "2.12.0",
    "org.mongodb.scala" %% "mongo-scala-driver"         % "5.1.1" cross CrossVersion.for3Use2_13
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-test-play-30"  % bootstrapVersion % Test,
    "org.jsoup"         %  "jsoup"                   % "1.18.1"         % Test,
    "org.scalatestplus" %% "scalacheck-1-18"         % "3.2.19.0"       % Test
  )
}
