package io.codegalaxy.app

import io.github.shogowada.scalajs.reactjs.React.Props
import scommons.react.UiComponent
import scommons.react.redux._
import scommons.react.redux.task.{TaskManager, TaskManagerProps}
import scommons.reactnative.app.AppTaskManagerUi
import scommons.reactnative.ui.popup.LoadingPopupProps

object CodeGalaxyTaskController
  extends BaseStateController[CodeGalaxyStateDef, TaskManagerProps] {

  lazy val uiComponent: UiComponent[TaskManagerProps] = {
    TaskManager.uiComponent = new AppTaskManagerUi(
      loadingProps = LoadingPopupProps(color = CodeGalaxyTheme.Colors.primary)
    )
    TaskManager.errorHandler = AppTaskManagerUi.errorHandler
    TaskManager
  }

  def mapStateToProps(dispatch: Dispatch, state: CodeGalaxyStateDef, props: Props[Unit]): TaskManagerProps = {
    TaskManagerProps(state.currentTask)
  }
}
