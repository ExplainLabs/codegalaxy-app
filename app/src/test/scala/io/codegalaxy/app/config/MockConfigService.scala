package io.codegalaxy.app.config

import io.codegalaxy.domain.ConfigEntity

import scala.concurrent.Future

//noinspection NotImplementedCode
class MockConfigService(
  getConfigMock: Int => Future[Option[ConfigEntity]] = _ => ???,
  setDarkThemeMock: (Int, Boolean) => Future[ConfigEntity] = (_, _) => ???
) extends ConfigService(null) {

  override def getConfig(userId: Int): Future[Option[ConfigEntity]] =
    getConfigMock(userId)
    
  override def setDarkTheme(userId: Int, darkTheme: Boolean): Future[ConfigEntity] =
    setDarkThemeMock(userId, darkTheme)
}
