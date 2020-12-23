package io.codegalaxy.api.chapter

import io.codegalaxy.api.stats.StatsData
import play.api.libs.json._

case class ChapterRespData(chapter: ChapterData)

object ChapterRespData {

  implicit val jsonReads: Reads[ChapterRespData] = Json.reads[ChapterRespData]
}

case class ChapterWithStatisticsRespData(chapter: ChapterData, stats: StatsData)

object ChapterWithStatisticsRespData {

  implicit val jsonReads: Reads[ChapterWithStatisticsRespData] = Json.reads[ChapterWithStatisticsRespData]
}
