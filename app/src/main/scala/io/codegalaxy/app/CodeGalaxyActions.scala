package io.codegalaxy.app

import io.codegalaxy.api.CodeGalaxyApiClient
import io.codegalaxy.app.chapter.{ChapterActions, ChapterService}
import io.codegalaxy.app.question.QuestionActions
import io.codegalaxy.app.topic.{TopicActions, TopicService}
import io.codegalaxy.app.user.{UserActions, UserService}
import io.codegalaxy.domain.CodeGalaxyDBContext
import io.codegalaxy.domain.dao._
import scommons.api.http.xhr.XhrApiHttpClient

class CodeGalaxyActions(ctx: CodeGalaxyDBContext)
  extends UserActions
  with TopicActions
  with ChapterActions
  with QuestionActions {

  protected val client: CodeGalaxyApiClient = {
    new CodeGalaxyApiClient(new XhrApiHttpClient("https://codegalaxy.io"))
  }

  protected val userService = new UserService(client, new ProfileDao(ctx))
  protected val topicService = new TopicService(client, new TopicDao(ctx))
  protected val chapterService = new ChapterService(client, new ChapterDao(ctx))
}
