package io.codegalaxy.api.chapter

import io.codegalaxy.api.stats.StatsData
import play.api.libs.json._

case class ChapterRespData(chapter: ChapterData,
                           stats: StatsData)

object ChapterRespData {

  implicit val jsonReads: Reads[ChapterRespData] = Json.reads[ChapterRespData]
}
