name := "ee-exercise-app"

version := "0.1"

scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "upickle" % "0.9.5",
  "org.xerial" % "sqlite-jdbc" % "3.36.0.3",
  "org.scalatest" %% "scalatest" % "3.2.10" % Test
)

// https://mvnrepository.com/artifact/org.apache.spark/spark-sql
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.5.1"