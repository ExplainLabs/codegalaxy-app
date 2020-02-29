package io.codegalaxy.app

import io.github.shogowada.scalajs.reactjs.React.Props
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react.redux.task._
import scommons.react.test.TestSpec

import scala.concurrent.Future

class CodeGalaxyTaskControllerSpec extends TestSpec {

  it should "return component" in {
    //when & then
    CodeGalaxyTaskController.uiComponent shouldBe TaskManager
  }

  it should "map state to props" in {
    //given
    val props = mock[Props[Unit]]
    val dispatch = mock[Dispatch]
    val currentTask = Some(FutureTask("test task", Future.successful(())))
    val state = mock[CodeGalaxyStateDef]
    (state.currentTask _).expects().returning(currentTask)

    //when
    val result = CodeGalaxyTaskController.mapStateToProps(dispatch, state, props)

    //then
    inside(result) { case TaskManagerProps(task) =>
      task shouldBe currentTask
    }
  }
}
