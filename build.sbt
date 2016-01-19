name := "sdbl-bal"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= {

  val akkaVersion       = "2.3.9"
  val reactiveMongoVersion = "0.11.9"
  val phantomVersion = "1.16.0"

  Seq(
    "com.typesafe.akka" %% "akka-actor"                 % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j"                 % akkaVersion,
    "org.reactivemongo" %% "reactivemongo"              % reactiveMongoVersion,
    "com.websudos"      %%  "phantom-dsl"               % phantomVersion,
    "com.websudos"      %%  "phantom-testkit"           % phantomVersion,
    "com.websudos"      %%  "phantom-connectors"        % phantomVersion,
    "eu.inn"            %%  "binders-cassandra"         % "0.2.5",
    "com.datastax.cassandra"  % "cassandra-driver-core" % "2.1.9" exclude("org.xerial.snappy", "snappy-java")
  )
}

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  Resolver.bintrayRepo("websudos", "oss-releases"),
  "spray repo"                       at "http://repo.spray.io",
  "Typesafe repository snapshots"    at "http://repo.typesafe.com/typesafe/snapshots/",
  "Typesafe repository releases"     at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype repo"                    at "https://oss.sonatype.org/content/groups/scala-tools/",
  "Sonatype releases"                at "https://oss.sonatype.org/content/repositories/releases",
  "Sonatype snapshots"               at "https://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype staging"                 at "http://oss.sonatype.org/content/repositories/staging",
  "Java.net Maven2 Repository"       at "http://download.java.net/maven/2/",
  "Twitter Repository"               at "http://maven.twttr.com"
)