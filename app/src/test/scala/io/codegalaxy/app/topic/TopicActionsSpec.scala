package io.codegalaxy.app.topic

import io.codegalaxy.api.topic._
import io.codegalaxy.app.topic.TopicActions._
import io.codegalaxy.app.topic.TopicActionsSpec._
import scommons.react.redux.task.FutureTask
import scommons.react.test.dom.AsyncTestSpec

import scala.concurrent.Future

class TopicActionsSpec extends AsyncTestSpec {

  it should "dispatch TopicsFetchedAction when fetchTopics" in {
    //given
    val api = mock[TopicApi]
    val actions = new TopicActionsTest(api)
    val dispatch = mockFunction[Any, Any]
    val topicData = TopicWithInfoData(
      alias = "test",
      name = "Test",
      language = "en",
      info = Some(TopicInfoData(
        numberOfQuestions = 1,
        numberOfPaid = 2,
        numberOfLearners = 3,
        numberOfChapters = 4
      ))
    )
    val svgIcon = "test svg"
    val items = List(TopicItemState(topicData, Some(svgIcon)))

    //then
    (api.getTopics _).expects(true).returning(Future.successful(List(topicData)))
    (api.getTopicIcon _).expects(topicData.alias).returning(Future.successful(Some(svgIcon)))
    dispatch.expects(TopicsFetchedAction(items))

    //when
    val TopicsFetchAction(FutureTask(message, future)) =
      actions.fetchTopics(dispatch)

    //then
    message shouldBe "Fetching Topics"
    future.map { resp =>
      resp shouldBe items
    }
  }
}

object TopicActionsSpec {

  private class TopicActionsTest(api: TopicApi)
    extends TopicActions {

    protected def client: TopicApi = api
  }
}
