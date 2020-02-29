package io.codegalaxy.app

import scommons.react.redux.task.{AbstractTask, TaskAction}

trait CodeGalaxyStateDef {

  def currentTask: Option[AbstractTask]
}

case class CodeGalaxyState(currentTask: Option[AbstractTask]) extends CodeGalaxyStateDef

object CodeGalaxyStateReducer {

  def reduce(state: Option[CodeGalaxyState], action: Any): CodeGalaxyState = CodeGalaxyState(
    currentTask = currentTaskReducer(state.flatMap(_.currentTask), action)
  )

  private def currentTaskReducer(currentTask: Option[AbstractTask],
                                 action: Any): Option[AbstractTask] = action match {
    case a: TaskAction => Some(a.task)
    case _ => currentTask
  }
}
