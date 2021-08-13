package io.codegalaxy.api.topic

import io.codegalaxy.api.data.InfoData
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json

class TopicWithInfoDataSpec extends AnyFlatSpec with Matchers {

  private val dataList = List(TopicWithInfoData(
    alias = "test_topic",
    name = "Test",
    language = "en",
    info = InfoData(
      numberOfQuestions = 1,
      numberOfPaid = 2,
      numberOfLearners = 3,
      numberOfChapters = 4,
      numberOfTheory = Some(5)
    )
  ))

  private val expectedJson = Json.prettyPrint(Json.parse(
    s"""[{
       |  "alias" : "test_topic",
       |  "name" : "Test",
       |  "language" : "en",
       |  "info" : {
       |    "numberOfQuestions" : 1,
       |    "numberOfPaid" : 2,
       |    "numberOfLearners" : 3,
       |    "numberOfChapters" : 4,
       |    "numberOfTheory" : 5
       |  }
       |}]""".stripMargin
  ))

  it should "deserialize data from json" in {
    //when & then
    Json.parse(expectedJson).as[List[TopicWithInfoData]] shouldBe dataList
  }
}
