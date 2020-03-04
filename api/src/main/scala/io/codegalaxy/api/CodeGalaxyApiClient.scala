package io.codegalaxy.api

import io.codegalaxy.api.user._
import scommons.api.http.{ApiHttpClient, ApiHttpStatusException}
import scommons.api.http.ApiHttpClient.queryParams
import scommons.api.http.ApiHttpData.UrlEncodedFormData
import scommons.api.http.ApiHttpMethod._

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
      case resp if resp.status != 200 =>
        throw ApiHttpStatusException("Login failed", resp.copy(body = ""))
      case _ => ()
    }
  }

  def logout(): Future[Unit] = {
    client.exec(GET, "/auth/logout", None).map {
      case resp if resp.status != 200 && resp.status != 303 =>
        throw ApiHttpStatusException("Logout failed", resp.copy(body = ""))
      case _ => ()
    }
  }

  def getUserProfile(force: Boolean): Future[Option[UserProfileData]] = {
    client.exec(GET, "/v1/profile", None, params = queryParams(
      "force" -> Some(force)
    )).map {
      case resp if resp.status == 401 => None
      case resp => Some(ApiHttpClient.parseResponse[UserProfileData](resp))
    }
  }
}
