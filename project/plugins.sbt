resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

//addSbtPlugin(("org.scommons.sbt" % "sbt-scommons-plugin" % "1.0.0-SNAPSHOT").changing())
addSbtPlugin("org.scommons.sbt" % "sbt-scommons-plugin" % "1.0.0")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.9.3")
addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.3.2")
