package io.codegalaxy.app.config

import io.codegalaxy.app.config.ConfigActions._
import io.codegalaxy.app.config.ConfigActionsSpec._
import io.codegalaxy.domain.ConfigEntity
import scommons.nodejs.test.AsyncTestSpec
import scommons.react.redux.task.FutureTask

import scala.concurrent.Future

class ConfigActionsSpec extends AsyncTestSpec {

  //noinspection TypeAnnotation
  class ConfigService {
    val setDarkTheme = mockFunction[Int, Boolean, Future[ConfigEntity]]

    val service = new MockConfigService(setDarkThemeMock = setDarkTheme)
  }

  it should "dispatch ConfigUpdatedAction when updateDarkTheme" in {
    //given
    val configService = new ConfigService
    val actions = new ConfigActionsTest(configService.service)
    val dispatch = mockFunction[Any, Any]
    val config = ConfigEntity(123, darkTheme = true)

    configService.setDarkTheme.expects(*, *).onCall { (id, t) =>
      id shouldBe 123
      t shouldBe true
      Future.successful(config)
    }

    //then
    dispatch.expects(ConfigUpdatedAction(config))

    //when
    val ConfigUpdateAction(FutureTask(message, future)) =
      actions.updateDarkTheme(dispatch, config.userId, config.darkTheme)

    //then
    message shouldBe "Updating darkTheme Config"
    future.map { res =>
      res shouldBe config
    }
  }
}

object ConfigActionsSpec {

  private class ConfigActionsTest(configServiceMock: ConfigService)
    extends ConfigActions {

    def configService: ConfigService = configServiceMock
  }
}
