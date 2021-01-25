name := "ClusteringServer"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  javaJdbc,
  evolutions,
  "mysql" % "mysql-connector-java" % "5.1.21"
)

//lazy val root = (project in file(".")).enablePlugins(PlayJava)

lazy val clusteringServer = (project in file("."))
  .enablePlugins(PlayJava, PlayEbean)
