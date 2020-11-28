package io.codegalaxy.domain

case class ConfigEntity(userId: Int,
                        darkTheme: Boolean)

//noinspection TypeAnnotation
trait ConfigSchema {

  val ctx: CodeGalaxyDBContext
  import ctx._

  implicit val configsUpdateMeta = updateMeta[ConfigEntity](
    _.userId
  )
  
  val configs = quote(querySchema[ConfigEntity]("configs"))
}
