package io.codegalaxy.api

import io.codegalaxy.api.user._
import scommons.api.http.ApiHttpClient.queryParams
import scommons.api.http.ApiHttpData.UrlEncodedFormData
import scommons.api.http.ApiHttpMethod._
import scommons.api.http.{ApiHttpClient, ApiHttpStatusException}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CodeGalaxyApiClient(client: ApiHttpClient)
  extends UserApi {

  ////////////////////////////////////////////////////////////////////////////////////////
  // user api

  def authenticate(user: String, password: String): Future[Unit] = {
    client.exec(POST, "/auth/authenticate/userpass", Some(UrlEncodedFormData(Map(
      "username" -> List(user),
      "password" -> List(password)
    )))).map {
      case resp if resp.status != 200 => throw ApiHttpStatusException("Login failed", resp)
      case _ => ()
    }
  }

  def getUserProfile(force: Boolean): Future[UserProfileData] = {
    client.execGet[UserProfileData]("/v1/profile", params = queryParams(
      "force" -> Some(force)
    ))
  }
}
