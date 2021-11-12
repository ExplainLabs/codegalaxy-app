import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
//import * as SplashScreen from 'expo-splash-screen';
import { registerRootComponent } from 'expo';

import { CodeGalaxyApp } from './target/scala-2.13/scalajs-bundler/main/codegalaxy-app-opt';

//SplashScreen.preventAutoHideAsync()

const App = new CodeGalaxyApp(() => {
  console.log("App is ready!")
  //SplashScreen.hideAsync()
}).apply()

registerRootComponent(App);
