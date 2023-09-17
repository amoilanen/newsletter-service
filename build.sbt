ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "3.3.0"

lazy val root = (project in file(".")).settings(
  name := "newsletter-service",
  libraryDependencies ++= Dependencies.allDependencies
)

lazy val integration = (project in file("integration"))
  .dependsOn(root)
  .settings(
    publish / skip := true,
    libraryDependencies ++= Dependencies.allDependencies
  )