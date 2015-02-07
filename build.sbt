name := "Tutorial_CGO2015"

version := "0.1"

scalaVersion := "2.11.5"

scalaSource in Compile <<= baseDirectory(_ / "src")

scalaSource in Test <<= baseDirectory(_ / "test-src")

libraryDependencies += "org.scala-lang" % "scala-library" % scalaVersion.value % "test"

libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value

libraryDependencies += "org.apache.commons" % "commons-math3" % "3.0"

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.12.1" % "test"

libraryDependencies += "lms" % "lms_2.11" % "0.4.004"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-Xlint")
