package io.codegalaxy.api

import io.codegalaxy.api.auth._
import scommons.api.http.ApiHttpData.UrlEncodedFormData
import scommons.api.http.ApiHttpMethod._
import scommons.api.http.{ApiHttpClient, ApiHttpStatusException}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CodeGalaxyApiClient(client: ApiHttpClient)
  extends AuthApi {

  ////////////////////////////////////////////////////////////////////////////////////////
  // auth api

  def authenticate(user: String, password: String): Future[String] = {
    client.exec(POST, "/auth/authenticate/userpass", Some(UrlEncodedFormData(Map(
      "username" -> List(user),
      "password" -> List(password)
    )))).map { resp =>
      if (resp.status != 200) throw ApiHttpStatusException("Login failed", resp)
      else {
        val headers = resp.headers.mkString("\n")
        println(s"headers:\n$headers")
        ""
      }
    }
  }
}
