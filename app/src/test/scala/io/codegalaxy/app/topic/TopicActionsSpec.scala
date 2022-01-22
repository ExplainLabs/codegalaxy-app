package io.codegalaxy.app.topic

import io.codegalaxy.app.topic.TopicActions._
import io.codegalaxy.app.topic.TopicActionsSpec._
import io.codegalaxy.domain.{Topic, TopicEntity}
import scommons.nodejs.test.AsyncTestSpec
import scommons.react.redux.task.FutureTask

import scala.concurrent.Future

class TopicActionsSpec extends AsyncTestSpec {

  //noinspection TypeAnnotation
  class TopicService {
    val fetch = mockFunction[Boolean, Future[Seq[Topic]]]
    
    val service = new MockTopicService(fetchMock = fetch)
  }
  
  it should "dispatch TopicsFetchedAction when fetchTopics" in {
    //given
    val topicService = new TopicService
    val actions = new TopicActionsTest(topicService.service)
    val dispatch = mockFunction[Any, Any]
    val topic = Topic(
      entity = TopicEntity(
        id = 1,
        alias = "test",
        name = "Test",
        lang = "en",
        numQuestions = 1,
        numPaid = 2,
        numLearners = 3,
        numChapters = 4,
        numTheory = Some(5),
        svgIcon = Some("test svg")
      ),
      stats = None
    )
    val dataList = List(topic)
    val refresh = true

    //then
    topicService.fetch.expects(*).onCall { (r: Boolean) =>
      r shouldBe refresh
      Future.successful(dataList)
    }
    dispatch.expects(TopicsFetchedAction(dataList))

    //when
    val TopicsFetchAction(FutureTask(message, future)) =
      actions.fetchTopics(dispatch, refresh)

    //then
    message shouldBe "Fetching Topics"
    future.map { resp =>
      resp shouldBe dataList
    }
  }
}

object TopicActionsSpec {

  private class TopicActionsTest(service: TopicService)
    extends TopicActions {

    protected def topicService: TopicService = service
  }
}
