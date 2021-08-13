
[![Build Status](https://travis-ci.com/ExplainLabs/codegalaxy-app.svg?branch=master)](https://travis-ci.com/ExplainLabs/codegalaxy-app)
[![Coverage Status](https://coveralls.io/repos/github/ExplainLabs/codegalaxy-app/badge.svg?branch=master)](https://coveralls.io/github/ExplainLabs/codegalaxy-app?branch=master)
[![Scala.js](https://www.scala-js.org/assets/badges/scalajs-0.6.29.svg)](https://www.scala-js.org)

## codegalaxy-app
Mobile App for https://codegalaxy.io/

![Screenshots](docs/images/screenshots.png)

### How to open it in your device (Android)

* [live expo link](https://expo.io/@viktorpodzigun/codegalaxy-app)

### Initial Setup:

Please, make sure you have `Node.js` **version 12.x** installed locally.

Then, please make sure you have `expo-cli` **version 3** installed
globally:
```bash
npm i expo-cli@3 -g
```

Then you can run following commands to init the project:

```bash
cd app
npm install
#Or
expo install
```

Or when upgrade/re-install the project, then perform these commands first:
```bash
sbt clean

cd app
rm -rf node_modules
```

#### Android Emulator Setup

For android emulator, please, follow these instructions:
* [Android emulator expo link](https://docs.expo.io/workflow/android-studio-emulator/)

#### How to Build/Run App locally using Expo

First, build the application with the following command:
```bash
sbt "project codegalaxy-app" fastOptJS
```

Then you can run it in emulator:
```bash
cd app
expo start --ios
#or:
expo start --android
```

#### How to Run App with reload workflow

First, build app with reload in sbt
```bash
sbt -mem 2048
sbt> project codegalaxy-app
codegalaxy-app> ~fastOptJS
```

Then, refresh app in Expo client:
* Cmd+R in emulator (if FastRefresh is disabled)
* or shake real device and press Reload in then menu

