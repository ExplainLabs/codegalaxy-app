
[![Build Status](https://travis-ci.org/viktor-podzigun/codegalaxy-app.svg?branch=master)](https://travis-ci.org/viktor-podzigun/codegalaxy-app)
[![Coverage Status](https://coveralls.io/repos/github/viktor-podzigun/codegalaxy-app/badge.svg?branch=master)](https://coveralls.io/github/viktor-podzigun/codegalaxy-app?branch=master)
[![Scala.js](https://www.scala-js.org/assets/badges/scalajs-0.6.17.svg)](https://www.scala-js.org)

## codegalaxy-app
Mobile App for https://codegalaxy.io/

### How to run it in your device

* [live expo link](https://expo.io/@viktorpodzigun/codegalaxy-app)

#### How to Build/Run App locally using Expo

First, build the application with the following command:
```bash
sbt "project codegalaxy-app" fastOptJS
```

To run the application locally, use the following command:
```bash
cd app
expo start --ios
expo start --android
```
