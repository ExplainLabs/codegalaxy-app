package io.codegalaxy.app

import io.codegalaxy.api.CodeGalaxyApiClient
import io.codegalaxy.app.chapter.{ChapterActions, ChapterService}
import io.codegalaxy.app.config.{ConfigActions, ConfigService}
import io.codegalaxy.app.question.QuestionActions
import io.codegalaxy.app.stats.StatsService
import io.codegalaxy.app.topic.{TopicActions, TopicService}
import io.codegalaxy.app.user.{UserActions, UserService}
import io.codegalaxy.domain.CodeGalaxyDBContext
import io.codegalaxy.domain.dao._
import scommons.api.http.xhr.XhrApiHttpClient

class CodeGalaxyActions(ctx: CodeGalaxyDBContext)
  extends UserActions
  with ConfigActions
  with TopicActions
  with ChapterActions
  with QuestionActions {

  protected val client: CodeGalaxyApiClient = {
    new CodeGalaxyApiClient(new XhrApiHttpClient("https://codegalaxy.io"))
  }

  private val configDao = new ConfigDao(ctx)
  private val profileDao = new ProfileDao(ctx)
  private val topicDao = new TopicDao(ctx)
  private val chapterDao = new ChapterDao(ctx)

  protected val configService = new ConfigService(configDao)
  protected val userService = new UserService(client, profileDao)
  protected val topicService = new TopicService(client, topicDao)
  protected val chapterService = new ChapterService(client, chapterDao)
  protected val statsService = new StatsService(client, topicDao, chapterDao)
}
