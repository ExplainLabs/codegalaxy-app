package io.codegalaxy.app.topic

import io.codegalaxy.app.topic.TopicActions.TopicsFetchedAction
import scommons.react.test.TestSpec

class TopicStateReducerSpec extends TestSpec {

  private val reduce = TopicStateReducer.apply _

  it should "return default state when state is None" in {
    //when & then
    reduce(None, "") shouldBe TopicState()
  }

  it should "set topics when TopicsFetchedAction" in {
    //given
    val dataList = List(mock[TopicItemState])

    //when & then
    reduce(Some(TopicState()), TopicsFetchedAction(dataList)) shouldBe TopicState(
      topics = dataList
    )
  }
}
