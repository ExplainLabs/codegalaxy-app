package io.codegalaxy.app

import io.codegalaxy.api.CodeGalaxyApiClient
import io.codegalaxy.app.topic.{TopicActions, TopicService}
import io.codegalaxy.app.user.UserActions
import io.codegalaxy.domain.CodeGalaxyDBContext
import io.codegalaxy.domain.dao.TopicDao
import scommons.api.http.xhr.XhrApiHttpClient

class CodeGalaxyActions(ctx: CodeGalaxyDBContext)
  extends UserActions
  with TopicActions {

  protected val client: CodeGalaxyApiClient = {
    new CodeGalaxyApiClient(new XhrApiHttpClient("https://codegalaxy.io"))
  }

  protected val topicService = new TopicService(client, new TopicDao(ctx))
}
