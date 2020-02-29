package io.codegalaxy.app

import io.codegalaxy.app.auth._
import io.github.shogowada.scalajs.reactjs.redux.ReactRedux._
import io.github.shogowada.scalajs.reactjs.redux.Redux
import scommons.react._

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel(name = "CodeGalaxyApp")
object CodeGalaxyApp extends FunctionComponent[Unit] {

  @JSExport("apply")
  override def apply(): ReactClass = super.apply()

  private val store = Redux.createStore(CodeGalaxyStateReducer.reduce)

  private lazy val actions = CodeGalaxyActions
  private lazy val authComp = new AuthController(actions).apply()
  
  protected def render(props: Props): ReactElement = {
    <.Provider(^.store := store)(
      <.>()(
        <(authComp).empty,
        <(CodeGalaxyTaskController()).empty
      )
    )
  }
}
