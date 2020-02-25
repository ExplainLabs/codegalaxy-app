import definitions._

lazy val `codegalaxy-app-root` = (project in file("."))
  .aggregate(
    `codegalaxy-api-jvm`,
    `codegalaxy-api-js`,
    `codegalaxy-app`
)

lazy val `codegalaxy-api-jvm` = CodeGalaxyApi.jvm
lazy val `codegalaxy-api-js` = CodeGalaxyApi.js
lazy val `codegalaxy-app` = CodeGalaxyApp.definition
