import sbt._

object CGOTutorialBuild extends Build {

  object Versions {
    val lms_branch = "refactor_immutable"
  }
  object Projects {
    lazy val lms_light = RootProject(uri("git://github.com/GeorgOfenbeck/virtualization-lms-core#%s".format(Versions.lms_branch)))
  }
  lazy val root =  Project("SpiralS", file("."))
    //.dependsOn(Projects.lms_light)
}