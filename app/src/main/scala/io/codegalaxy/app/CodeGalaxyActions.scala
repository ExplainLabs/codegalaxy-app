package io.codegalaxy.app

import io.codegalaxy.api.CodeGalaxyApiClient
import io.codegalaxy.app.auth.AuthActions
import scommons.api.http.dom.DomApiHttpClient

trait CodeGalaxyActions
  extends AuthActions

object CodeGalaxyActions extends CodeGalaxyActions {

  protected val client: CodeGalaxyApiClient = {
    new CodeGalaxyApiClient(new DomApiHttpClient("https://codegalaxy.io"))
  }
}
