package io.codegalaxy.app.config

import io.codegalaxy.domain.ConfigEntity
import io.codegalaxy.domain.dao.ConfigDao

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ConfigService(dao: ConfigDao) {

  def getConfig(userId: Int): Future[Option[ConfigEntity]] = {
    dao.getByUserId(userId)
  }
  
  def setDarkTheme(userId: Int, darkTheme: Boolean): Future[ConfigEntity] = {
    for {
      maybeConfig <- dao.getByUserId(userId)
      entity = maybeConfig match {
        case None => ConfigEntity(userId, darkTheme)
        case Some(config) => config.copy(darkTheme = darkTheme)
      }
      res <- dao.save(entity)
    } yield {
      res
    }
  }
}
