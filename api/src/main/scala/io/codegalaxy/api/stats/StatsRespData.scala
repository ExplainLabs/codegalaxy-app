package io.codegalaxy.api.stats

import play.api.libs.json._

case class StatsRespData(topic: StatsTopicData,
                         statistics: StatsData)

object StatsRespData {

  implicit val jsonReads: Reads[StatsRespData] = Json.reads[StatsRespData]
}
