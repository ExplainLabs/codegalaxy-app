package io.codegalaxy.app

import io.codegalaxy.app.user.{UserState, UserStateReducer}
import scommons.react.redux.task.{AbstractTask, TaskAction}

trait CodeGalaxyStateDef {

  def currentTask: Option[AbstractTask]
  def userState: UserState
}

case class CodeGalaxyState(currentTask: Option[AbstractTask],
                           userState: UserState) extends CodeGalaxyStateDef

object CodeGalaxyStateReducer {

  def reduce(state: Option[CodeGalaxyState], action: Any): CodeGalaxyState = CodeGalaxyState(
    currentTask = currentTaskReducer(state.flatMap(_.currentTask), action),
    userState = UserStateReducer(state.map(_.userState), action)
  )

  private def currentTaskReducer(currentTask: Option[AbstractTask],
                                 action: Any): Option[AbstractTask] = action match {
    case a: TaskAction => Some(a.task)
    case _ => currentTask
  }
}
