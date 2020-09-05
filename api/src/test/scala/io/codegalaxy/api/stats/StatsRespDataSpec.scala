package io.codegalaxy.api.stats

import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.Json

class StatsRespDataSpec extends FlatSpec with Matchers {

  private val dataList = List(StatsRespData(
    topic = StatsTopicData(
      alias = "test_topic"
    ),
    statistics = StatsData(
      progressAll = 88
    )
  ))

  private val expectedJson = Json.prettyPrint(Json.parse(
    s"""[{
       |  "topic" : {
       |    "alias" : "test_topic"
       |  },
       |  "statistics" : {
       |    "progressAll" : 88
       |  }
       |}]""".stripMargin
  ))

  it should "deserialize data from json" in {
    //when & then
    Json.parse(expectedJson).as[List[StatsRespData]] shouldBe dataList
  }
}
