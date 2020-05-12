package io.codegalaxy.api.user

import play.api.libs.json._

case class UserProfileData(userId: Int,
                           username: String,
                           city: Option[String],
                           firstName: Option[String],
                           lastName: Option[String])

object UserProfileData {

  implicit val jsonFormat: Format[UserProfileData] = Json.format[UserProfileData]
}
