package io.codegalaxy.api.auth

import scala.concurrent.Future

trait AuthApi {

  def authenticate(user: String, password: String): Future[String]
}
