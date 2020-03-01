package io.codegalaxy.app

import io.codegalaxy.api.CodeGalaxyApiClient
import io.codegalaxy.app.user.UserActions
import scommons.api.http.dom.DomApiHttpClient

trait CodeGalaxyActions
  extends UserActions {

  protected val client: CodeGalaxyApiClient
}

object CodeGalaxyActions extends CodeGalaxyActions {

  protected val client: CodeGalaxyApiClient = {
    new CodeGalaxyApiClient(new DomApiHttpClient("https://codegalaxy.io"))
  }
}
