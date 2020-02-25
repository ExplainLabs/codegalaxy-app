import React from 'react';
import { StyleSheet, Text, View } from 'react-native';

import {CodeGalaxyApp} from './target/scala-2.12/scalajs-bundler/main/codegalaxy-app-fastopt';

const App = CodeGalaxyApp.apply()

export default () => {
  
  return <App />
}
