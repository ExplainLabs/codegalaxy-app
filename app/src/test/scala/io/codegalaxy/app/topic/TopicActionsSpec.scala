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
    val topicsResp = List(mock[TopicWithInfoData])

    //then
    (api.getTopics _).expects(true).returning(Future.successful(topicsResp))
    dispatch.expects(TopicsFetchedAction(topicsResp))

    //when
    val TopicsFetchAction(FutureTask(message, future)) =
      actions.fetchTopics(dispatch)

    //then
    message shouldBe "Fetching Topics"
    future.map { resp =>
      resp shouldBe topicsResp
    }
  }
}

object TopicActionsSpec {

  private class TopicActionsTest(api: TopicApi)
    extends TopicActions {

    protected def client: TopicApi = api
  }
}
