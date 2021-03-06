
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
  filters,
  "org.codehaus.jackson" % "jackson-mapper-asl" % "1.8.5",
  "org.yaml" % "snakeyaml" % "1.16",
  "org.bitbucket.b_c" % "jose4j" % "0.4.4",
  "com.typesafe.play" %% "play-mailer" % "3.0.1",
  "commons-io" % "commons-io" % "2.4",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.dropbox.core" % "dropbox-core-sdk" % "[1.7,1.8)",
  "com.pusher" % "pusher-http-java" % "0.9.3"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

// Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
//EclipseKeys.preTasks := Seq(compile in Compile)

herokuAppName in Compile := "recetarium"
javaOptions in Test += "-Dlogger.file=conf/test-logback.xml"
javaOptions in Test += "-Dconfig.file=conf/test.conf"
javaOptions in Production += "-Dconfig.file=conf/prod.conf"
