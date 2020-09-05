package io.codegalaxy.api.user

import play.api.libs.json._

case class UserData(username: String,
                    email: Option[String],
                    fullName: Option[String],
                    avatarUrl: Option[String])

object UserData {

  implicit val jsonReads: Reads[UserData] = Json.reads[UserData]
}
