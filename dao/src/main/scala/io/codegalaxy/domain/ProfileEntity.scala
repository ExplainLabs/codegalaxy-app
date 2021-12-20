package io.codegalaxy.domain

case class ProfileEntity(id: Int,
                         username: String,
                         email: Option[String],
                         firstName: Option[String],
                         lastName: Option[String],
                         fullName: Option[String],
                         city: Option[String],
                         avatarUrl: Option[String])
