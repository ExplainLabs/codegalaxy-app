package io.codegalaxy.app.chapter

import io.codegalaxy.app.BaseStateReducerSpec
import io.codegalaxy.app.chapter.ChapterActions._
import io.codegalaxy.domain.ChapterEntity
import scommons.react.redux.task.FutureTask

import scala.concurrent.Future

class ChapterStateReducerSpec extends BaseStateReducerSpec(
  createState = ChapterState(),
  reduce = ChapterStateReducer.apply
) {

  it should "set topic and reset chapters when ChaptersFetchAction" in {
    //given
    val topic = "test_topic"
    val dataList = Seq(mock[ChapterEntity])
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
    val dataList = List(mock[ChapterEntity])

    //when & then
    reduce(Some(ChapterState()), ChaptersFetchedAction(topic, dataList)) shouldBe ChapterState(
      topic = Some(topic),
      chapters = dataList
    )
  }
}
