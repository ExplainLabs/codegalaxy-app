package io.codegalaxy.api.user

import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.Json

class UserProfileDataSpec extends FlatSpec with Matchers {

  private val data = UserProfileData(
    userId = 123,
    username = "test_user",
    city = Some("test city"),
    firstName = Some("test firstName"),
    lastName = Some("test lastName")
  )

  private val expectedJson = Json.prettyPrint(Json.parse(
    s"""{
       |  "userId" : 123,
       |  "username" : "test_user",
       |  "city" : "test city",
       |  "firstName" : "test firstName",
       |  "lastName" : "test lastName"
       |}""".stripMargin
  ))

  it should "deserialize data from json" in {
    //when & then
    Json.parse(expectedJson).as[UserProfileData] shouldBe data
  }
}
