name := """recetarium"""

version := "alpha"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJpa,
  "org.hibernate" % "hibernate-entitymanager" % "4.3.7.Final",
  "mysql" % "mysql-connector-java" % "5.1.25",
  "org.dbunit" % "dbunit" % "2.4.9",
  cache,
  javaWs,
  "org.codehaus.jackson" % "jackson-mapper-asl" % "1.8.5",
  "org.yaml" % "snakeyaml" % "1.16"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

// Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
//EclipseKeys.preTasks := Seq(compile in Compile)

herokuAppName in Compile := "recetarium"
