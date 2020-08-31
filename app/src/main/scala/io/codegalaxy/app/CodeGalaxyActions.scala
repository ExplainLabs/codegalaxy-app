package io.codegalaxy.app

import io.codegalaxy.api.CodeGalaxyApiClient
import io.codegalaxy.app.topic.{TopicActions, TopicService}
import io.codegalaxy.app.user.{UserActions, UserService}
import io.codegalaxy.domain.CodeGalaxyDBContext
import io.codegalaxy.domain.dao.{ProfileDao, TopicDao}
import scommons.api.http.xhr.XhrApiHttpClient

class CodeGalaxyActions(ctx: CodeGalaxyDBContext)
  extends UserActions
  with TopicActions {

  protected val client: CodeGalaxyApiClient = {
    new CodeGalaxyApiClient(new XhrApiHttpClient("https://codegalaxy.io"))
  }

  protected val userService = new UserService(client, new ProfileDao(ctx))
  protected val topicService = new TopicService(client, new TopicDao(ctx))
}
