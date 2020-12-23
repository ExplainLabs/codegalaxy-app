package io.codegalaxy.app.topic

import io.codegalaxy.app.BaseStateReducerSpec
import io.codegalaxy.app.stats.StatsActions.StatsFetchedAction
import io.codegalaxy.app.topic.TopicActions.TopicsFetchedAction
import io.codegalaxy.domain.{ChapterStats, Topic, TopicEntity, TopicStats}

class TopicStateReducerSpec extends BaseStateReducerSpec(
  createState = TopicState(),
  reduce = TopicStateReducer.apply
) {

  it should "set topics when TopicsFetchedAction" in {
    //given
    val dataList = List(mock[Topic])

    //when & then
    reduce(Some(TopicState()), TopicsFetchedAction(dataList)) shouldBe TopicState(
      topics = dataList
    )
  }
  
  it should "update topic stats when StatsFetchedAction" in {
    //given
    val topicStats = TopicStats(
      id = 2,
      progress = 11,
      progressOnce = 22,
      progressAll = 33,
      freePercent = 44,
      paid = 55
    )
    val chapterStats = mock[ChapterStats]
    val stats = (topicStats, chapterStats)
    val dataList = List(
      Topic(
        entity = TopicEntity(
          id = 1,
          alias = "test_topic",
          name = "Test Topic",
          lang = "en",
          numQuestions = 1,
          numPaid = 2,
          numLearners = 3,
          numChapters = 4,
          numTheory = Some(5),
          svgIcon = Some("svg-xml")
        ),
        stats = None
      ),
      Topic(
        entity = TopicEntity(
          id = 2,
          alias = "test_topic2",
          name = "Test Topic2",
          lang = "en",
          numQuestions = 11,
          numPaid = 22,
          numLearners = 33,
          numChapters = 44,
          numTheory = Some(5),
          svgIcon = Some("svg-xml2")
        ),
        stats = Some(TopicStats(
          id = 2,
          progress = 1,
          progressOnce = 2,
          progressAll = 3,
          freePercent = 4,
          paid = 5
        ))
      )
    )

    //when & then
    reduce(Some(TopicState(dataList)), StatsFetchedAction(stats)) shouldBe TopicState(
      topics = List(
        dataList.head,
        dataList(1).copy(stats = Some(topicStats))
      )
    )
  }
}
