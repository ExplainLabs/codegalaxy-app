package io.codegalaxy.api.user

import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.Json

class UserDataSpec extends FlatSpec with Matchers {

  private val data = UserData(
    username = "test_user",
    email = Some("test email"),
    fullName = Some("Test FullName"),
    avatarUrl = Some("/test/avatar/url")
  )

  private val expectedJson = Json.prettyPrint(Json.parse(
    s"""{
       |  "username" : "test_user",
       |  "email" : "test email",
       |  "fullName" : "Test FullName",
       |  "avatarUrl" : "/test/avatar/url"
       |}""".stripMargin
  ))

  it should "deserialize data from json" in {
    //when & then
    Json.parse(expectedJson).as[UserData] shouldBe data
  }
}
