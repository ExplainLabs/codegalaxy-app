package io.codegalaxy.app

import scommons.react.navigation.Navigation

import scala.scalajs.js

//noinspection NotImplementedCode
class MockNavigation(
  navigateMock: String => Unit = _ => ???,
  navigate2Mock: (String, Map[String, String]) => Unit = (_, _) => ???,
  goBackMock: () => Unit = () => ???,
  getParamsMock: () => Map[String, String] = () => ???,
  setParamsMock: Map[String, String] => Unit = _ => ???,
  setOptionsMock: js.Object => Unit = _ => ???
) extends Navigation(null, null) {

  override def navigate(name: String): Unit = navigateMock(name)

  override def navigate(name: String, params: Map[String, String]): Unit = navigate2Mock(name, params)

  override def goBack(): Unit = goBackMock()

  override def getParams: Map[String, String] = getParamsMock()

  override def setParams(params: Map[String, String]): Unit = setParamsMock(params)

  override def setOptions(options: js.Object): Unit = setOptionsMock(options)
}
