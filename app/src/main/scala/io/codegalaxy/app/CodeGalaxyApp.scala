package io.codegalaxy.app

import io.codegalaxy.app.auth._
import scommons.react._

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel(name = "CodeGalaxyApp")
object CodeGalaxyApp extends FunctionComponent[Unit] {

  @JSExport("apply")
  override def apply(): ReactClass = super.apply()

  protected def render(props: Props): ReactElement = {
    <(AuthScreen())(^.wrapped := AuthScreenProps(
      onLogin = { (_, _) =>
      }
    ))()
  }
}
