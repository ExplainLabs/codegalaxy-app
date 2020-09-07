package io.codegalaxy.app.topic

import io.codegalaxy.app.BaseStateReducerSpec
import io.codegalaxy.app.topic.TopicActions.TopicsFetchedAction
import io.codegalaxy.domain.TopicEntity

class TopicStateReducerSpec extends BaseStateReducerSpec(
  createState = TopicState(),
  reduce = TopicStateReducer.apply
) {

  it should "set topics when TopicsFetchedAction" in {
    //given
    val dataList = List(mock[TopicEntity])

    //when & then
    reduce(Some(TopicState()), TopicsFetchedAction(dataList)) shouldBe TopicState(
      topics = dataList
    )
  }
}
