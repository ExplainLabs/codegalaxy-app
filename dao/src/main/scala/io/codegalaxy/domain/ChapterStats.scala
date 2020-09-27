package io.codegalaxy.domain

case class ChapterStats(id: Int,
                        progress: Int,
                        progressOnce: Int,
                        progressAll: Int,
                        freePercent: Int,
                        paid: Int)

//noinspection TypeAnnotation
trait ChapterStatsSchema {

  val ctx: CodeGalaxyDBContext
  import ctx._

  val chaptersStats = quote(querySchema[ChapterStats]("chapters_stats"))
}
