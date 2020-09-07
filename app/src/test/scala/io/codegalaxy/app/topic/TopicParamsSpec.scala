package io.codegalaxy.app.topic

import org.scalatest.{FlatSpec, Matchers}

class TopicParamsSpec extends FlatSpec with Matchers {

  private val params = TopicParams(
    topic = "test_topic",
    chapter = Some("test_chapter")
  )
  
  private val expectedMap = Map(
    "topic" -> "test_topic",
    "chapter" -> "test_chapter"
  )

  it should "serialize params when toMap" in {
    //when & then
    params.toMap shouldBe expectedMap
  }
  
  it should "deserialize params when fromMap" in {
    //when & then
    TopicParams.fromMap(expectedMap) shouldBe params
  }
  
  it should "fail if chapter is not specified when getChapter" in {
    //given
    val params = TopicParams("test_topic")

    //when
    val ex = the[IllegalArgumentException] thrownBy {
      params.getChapter
    }

    //then
    ex.getMessage shouldBe s"Chapter is not specified, params: $params"
  }
}
