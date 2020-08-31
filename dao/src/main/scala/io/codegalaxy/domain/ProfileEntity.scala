package io.codegalaxy.domain

case class ProfileEntity(id: Int,
                         username: String,
                         email: Option[String],
                         firstName: Option[String],
                         lastName: Option[String],
                         fullName: Option[String],
                         city: Option[String],
                         avatarUrl: Option[String])

//noinspection TypeAnnotation
trait ProfileSchema {

  val ctx: CodeGalaxyDBContext
  import ctx._

  val profiles = quote(querySchema[ProfileEntity]("profiles"))
}
