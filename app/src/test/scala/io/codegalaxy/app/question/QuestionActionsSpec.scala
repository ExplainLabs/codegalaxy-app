package io.codegalaxy.app.question

import io.codegalaxy.api.question._
import io.codegalaxy.app.question.QuestionActions._
import io.codegalaxy.app.question.QuestionActionsSpec._
import scommons.nodejs.test.AsyncTestSpec
import scommons.react.redux.task.FutureTask

import scala.concurrent.Future

class QuestionActionsSpec extends AsyncTestSpec {

  it should "dispatch QuestionFetchedAction when fetchQuestion" in {
    //given
    val api = mock[QuestionApi]
    val actions = new QuestionActionsTest(api)
    val dispatch = mockFunction[Any, Any]
    val topic = "test_topic"
    val chapter = "test_chapter"
    val data = mock[QuestionData]

    //then
    (api.getNextQuestion _).expects(topic, chapter).returning(Future.successful(data))
    dispatch.expects(QuestionFetchedAction(topic, chapter, data))

    //when
    val QuestionFetchAction(resTopic, resChapter, FutureTask(message, future)) =
      actions.fetchQuestion(dispatch, topic, chapter)

    //then
    resTopic shouldBe topic
    resChapter shouldBe chapter
    message shouldBe "Fetching Question"
    future.map { resp =>
      resp shouldBe data
    }
  }
  
  it should "dispatch QuestionFetchedAction when submitAnswer" in {
    //given
    val api = mock[QuestionApi]
    val actions = new QuestionActionsTest(api)
    val dispatch = mockFunction[Any, Any]
    val topic = "test_topic"
    val chapter = "test_chapter"
    val data = mock[QuestionData]
    val respData = mock[QuestionData]

    //then
    (api.submitAnswer _).expects(topic, chapter, data).returning(Future.successful(respData))
    dispatch.expects(QuestionFetchedAction(topic, chapter, respData))

    //when
    val AnswerSubmitAction(FutureTask(message, future)) =
      actions.submitAnswer(dispatch, topic, chapter, data)

    //then
    message shouldBe "Submitting Answer"
    future.map { resp =>
      resp shouldBe respData
    }
  }
}

object QuestionActionsSpec {

  private class QuestionActionsTest(api: QuestionApi)
    extends QuestionActions {

    protected def client: QuestionApi = api
  }
}
