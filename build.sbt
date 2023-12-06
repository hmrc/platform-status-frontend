import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "2.13.12"

lazy val microservice = Project("platform-status-frontend", file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scalacOptions       += "-Wconf:cat=unused-imports&src=html/.*:s",
    scalacOptions       += "-Wconf:src=routes/.*:s"
  )
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(PlayKeys.playDefaultPort := 8462)

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(DefaultBuildSettings.itSettings)
  .settings(libraryDependencies ++= AppDependencies.test)
