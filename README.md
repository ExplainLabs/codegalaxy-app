
[![Build Status](https://travis-ci.com/ExplainLabs/codegalaxy-app.svg?branch=master)](https://travis-ci.com/ExplainLabs/codegalaxy-app)
[![Coverage Status](https://coveralls.io/repos/github/ExplainLabs/codegalaxy-app/badge.svg?branch=master)](https://coveralls.io/github/ExplainLabs/codegalaxy-app?branch=master)
[![Scala.js](https://www.scala-js.org/assets/badges/scalajs-0.6.17.svg)](https://www.scala-js.org)

## codegalaxy-app
Mobile App for https://codegalaxy.io/

![Screenshots](docs/images/screenshots.png)

### How to open it in your device (Android)

* [live expo link](https://expo.io/@viktorpodzigun/codegalaxy-app)

#### Android Emulator Setup
* [Android emulator expo link](https://docs.expo.io/workflow/android-studio-emulator/)

#### How to Build/Run App locally using Expo

First, build the application with the following command:
```bash
sbt "project codegalaxy-app" fastOptJS
```

Then start the application locally, in simulator:
```bash
cd app
npm install
expo install
expo start --ios
#or:
expo start --android
```

Reload:
```bash
cd app
rm -rf node_modules
cd ../
sbt clean
```
