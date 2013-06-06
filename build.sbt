crossScalaVersions := Seq("2.10.1")

scalaVersion <<= (crossScalaVersions) {
      versions => versions.head
    }

resolvers ++= Seq(Opts.resolver.sonatypeReleases)

resolvers += "Activate Framework" at "http://fwbrasil.net/maven/"

resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/repo/"


libraryDependencies ++= Seq(
    "net.fwbrasil" %% "activate-jdbc" % "1.3-SNAPSHOT" changing(),
    "net.fwbrasil" %% "activate-core" % "1.3-SNAPSHOT" changing(),
    "org.scalatra" %% "scalatra" % "2.2.1" % "test",
    "org.scalatra" %% "scalatra-scalatest" % "2.2.1" % "test",
    "com.fasterxml.jackson.core" % "jackson-core" % "2.2.1",
    "com.fasterxml.jackson.core" % "jackson-annotations" % "2.2.1",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.2.1",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.2.1",
    "com.h2database" % "h2" % "1.3.170"
)

