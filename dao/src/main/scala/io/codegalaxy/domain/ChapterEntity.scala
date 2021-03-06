package io.codegalaxy.domain

case class ChapterEntity(id: Int,
                         topic: String,
                         alias: String,
                         name: String,
                         numQuestions: Int,
                         numPaid: Int,
                         numLearners: Int,
                         numChapters: Int,
                         numTheory: Option[Int])

//noinspection TypeAnnotation
trait ChapterSchema {

  val ctx: CodeGalaxyDBContext
  import ctx._

  implicit val chaptersInsertMeta = insertMeta[ChapterEntity](
    _.id
  )
  implicit val chaptersUpdateMeta = updateMeta[ChapterEntity](
    _.id
  )

  val chapters = quote(querySchema[ChapterEntity]("chapters"))
}
