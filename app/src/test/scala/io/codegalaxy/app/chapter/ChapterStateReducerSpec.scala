package io.codegalaxy.app.chapter

import io.codegalaxy.app.BaseStateReducerSpec
import io.codegalaxy.app.chapter.ChapterActions._
import io.codegalaxy.app.stats.StatsActions.StatsFetchedAction
import io.codegalaxy.domain.{Chapter, ChapterEntity, ChapterStats, TopicStats}
import scommons.react.redux.task.FutureTask

import scala.concurrent.Future

class ChapterStateReducerSpec extends BaseStateReducerSpec(
  createState = ChapterState(),
  reduce = ChapterStateReducer.apply
) {

  it should "set topic and reset chapters when ChaptersFetchAction" in {
    //given
    val topic = "test_topic"
    val dataList = Seq(mock[Chapter])
    val state = ChapterState(chapters = dataList)
    val task = FutureTask("Fetching...", Future.successful(dataList))

    //when & then
    reduce(Some(state), ChaptersFetchAction(topic, task)) shouldBe ChapterState(
      topic = Some(topic),
      chapters = Nil
    )
  }
  
  it should "set topic and chapters when ChaptersFetchedAction" in {
    //given
    val topic = "test_topic"
    val dataList = List(mock[Chapter])

    //when & then
    reduce(Some(ChapterState()), ChaptersFetchedAction(topic, dataList)) shouldBe ChapterState(
      topic = Some(topic),
      chapters = dataList
    )
  }
  
  it should "update chapter stats when StatsFetchedAction" in {
    //given
    val topic = "test_topic"
    val topicStats = mock[TopicStats]
    val chapterStats = ChapterStats(
      id = 2,
      progress = 11,
      progressOnce = 22,
      progressAll = 33,
      freePercent = 44,
      paid = 55
    )
    val stats = (topicStats, chapterStats)
    val dataList = List(
      Chapter(
        entity = ChapterEntity(
          id = 1,
          topic = "test_topic",
          alias = "test_chapter",
          name = "Test Chapter",
          numQuestions = 1,
          numPaid = 2,
          numLearners = 3,
          numChapters = 4,
          numTheory = Some(5)
        ),
        stats = None
      ),
      Chapter(
        entity = ChapterEntity(
          id = 2,
          topic = "test_topic",
          alias = "test_chapter2",
          name = "Test Chapter2",
          numQuestions = 0,
          numPaid = 2,
          numLearners = 5,
          numChapters = 4,
          numTheory = Some(5)
        ),
        stats = Some(ChapterStats(
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
    reduce(Some(ChapterState(Some(topic), dataList)), StatsFetchedAction(stats)) shouldBe ChapterState(
      topic = Some(topic),
      chapters = List(
        dataList.head,
        dataList(1).copy(stats = Some(chapterStats))
      )
    )
  }
}
