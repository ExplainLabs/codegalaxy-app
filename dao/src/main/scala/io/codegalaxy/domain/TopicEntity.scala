package io.codegalaxy.domain

case class TopicEntity(id: Int,
                       alias: String,
                       name: String,
                       lang: String,
                       numQuestions: Int,
                       numPaid: Int,
                       numLearners: Int,
                       numChapters: Int,
                       svgIcon: Option[String])

//noinspection TypeAnnotation
trait TopicSchema {

  val ctx: CodeGalaxyDBContext
  import ctx._

  implicit val topicsInsertMeta = insertMeta[TopicEntity](
    _.id
  )
  implicit val topicsUpdateMeta = updateMeta[TopicEntity](
    _.id
  )

  val topics = quote(querySchema[TopicEntity]("topics"))
}
