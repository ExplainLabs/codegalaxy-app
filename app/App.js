import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { SplashScreen } from 'expo';

import { CodeGalaxyApp } from './target/scala-2.12/scalajs-bundler/main/codegalaxy-app-fastopt';

SplashScreen.preventAutoHide()

const App = new CodeGalaxyApp(() => {
  console.log("App is ready!")
  SplashScreen.hide()
}).apply()

export default () => {
  
  return <App />
}
