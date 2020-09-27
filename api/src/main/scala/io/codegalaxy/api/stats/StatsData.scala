package io.codegalaxy.api.stats

import play.api.libs.json._

case class StatsData(progress: Int,
                     progressOnce: Int,
                     progressAll: Int,
                     freePercent: Int,
                     paid: Int)

object StatsData {

  implicit val jsonReads: Reads[StatsData] = Json.reads[StatsData]
}
