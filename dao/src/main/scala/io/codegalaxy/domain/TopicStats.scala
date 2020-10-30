package io.codegalaxy.domain

case class TopicStats(id: Int,
                      progress: Int,
                      progressOnce: Int,
                      progressAll: Int,
                      freePercent: Int,
                      paid: Int)

//noinspection TypeAnnotation
trait TopicStatsSchema {

  val ctx: CodeGalaxyDBContext
  import ctx._

  val topicsStats = quote(querySchema[TopicStats]("topics_stats"))
}
