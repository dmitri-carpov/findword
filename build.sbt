name := "findword"

scalaVersion := "2.11.2"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.5" % "test",
  "org.scalacheck" % "scalacheck_2.11" % "1.11.6" % "test"
)