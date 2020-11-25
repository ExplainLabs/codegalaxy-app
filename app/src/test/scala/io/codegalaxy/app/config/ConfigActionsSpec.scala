package io.codegalaxy.app.config

import io.codegalaxy.app.config.ConfigActions._
import io.codegalaxy.app.config.ConfigActionsSpec._
import scommons.nodejs.test.AsyncTestSpec
import scommons.react.redux.task.FutureTask

class ConfigActionsSpec extends AsyncTestSpec {

  it should "dispatch ConfigUpdatedAction when updateConfig" in {
    //given
    val actions = new ConfigActionsTest
    val dispatch = mockFunction[Any, Any]
    val userId = 123
    val darkTheme = true

    //then
    dispatch.expects(ConfigUpdatedAction(darkTheme))

    //when
    val ConfigUpdateAction(FutureTask(message, future)) =
      actions.updateConfig(dispatch, userId, darkTheme)

    //then
    message shouldBe "Updating Config"
    future.map { resp =>
      resp shouldBe darkTheme
    }
  }
}

object ConfigActionsSpec {

  private class ConfigActionsTest extends ConfigActions {

  }
}
