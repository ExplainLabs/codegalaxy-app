package io.codegalaxy.app

import io.codegalaxy.api.CodeGalaxyApiClient
import io.codegalaxy.app.topic.TopicActions
import io.codegalaxy.app.user.UserActions
import scommons.api.http.xhr.XhrApiHttpClient

trait CodeGalaxyActions
  extends UserActions
  with TopicActions {

  protected def client: CodeGalaxyApiClient
}

object CodeGalaxyActions extends CodeGalaxyActions {

  protected val client: CodeGalaxyApiClient = {
    new CodeGalaxyApiClient(new XhrApiHttpClient("https://codegalaxy.io"))
  }
}
