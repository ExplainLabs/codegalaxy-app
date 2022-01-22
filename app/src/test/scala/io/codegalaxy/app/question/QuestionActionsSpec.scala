package io.codegalaxy.app.question

import io.codegalaxy.api.question._
import io.codegalaxy.app.question.QuestionActions._
import io.codegalaxy.app.question.QuestionActionsSpec._
import io.codegalaxy.app.stats.StatsActions.StatsFetchedAction
import io.codegalaxy.app.stats.{MockStatsService, StatsService}
import io.codegalaxy.domain.{ChapterStats, TopicStats}
import scommons.nodejs.test.AsyncTestSpec
import scommons.react.redux.task.FutureTask

import scala.concurrent.Future

class QuestionActionsSpec extends AsyncTestSpec {

  //noinspection TypeAnnotation
  class Api {
    val getNextQuestion = mockFunction[String, String, Future[QuestionData]]
    val submitAnswer = mockFunction[String, String, QuestionData, Future[QuestionData]]

    val api = new MockQuestionApi(
      getNextQuestionMock = getNextQuestion,
      submitAnswerMock = submitAnswer
    )
  }

  //noinspection TypeAnnotation
  class StatsService {
    val updateStats = mockFunction[String, String, Future[(TopicStats, ChapterStats)]]

    val service = new MockStatsService(updateStatsMock = updateStats)
  }

  it should "dispatch QuestionFetchedAction when fetchQuestion" in {
    //given
    val api = new Api
    val statsService = new StatsService
    val actions = new QuestionActionsTest(api.api, statsService.service)
    val dispatch = mockFunction[Any, Any]
    val topic = "test_topic"
    val chapter = "test_chapter"
    val data = QuestionData(
      uuid = "1",
      text = "Can methods, taking one argument, be used with infix syntax?",
      answerType = "SINGLE_CHOICE",
      choices = Nil
    )

    //then
    api.getNextQuestion.expects(*, *).onCall { (t, c) =>
      t shouldBe topic
      c shouldBe chapter
      Future.successful(data)
    }
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
  
  it should "dispatch QuestionFetchedAction and StatsFetchedAction when submitAnswer" in {
    //given
    val api = new Api
    val statsService = new StatsService
    val actions = new QuestionActionsTest(api.api, statsService.service)
    val dispatch = mockFunction[Any, Any]
    val topic = "test_topic"
    val chapter = "test_chapter"
    val data = QuestionData(
      uuid = "1",
      text = "Can methods, taking one argument, be used with infix syntax?",
      answerType = "SINGLE_CHOICE",
      choices = Nil
    )
    val respData = QuestionData(
      uuid = "2",
      text = "test",
      answerType = "SINGLE_CHOICE",
      choices = Nil
    )
    val topicStats = mock[TopicStats]
    val chapterStats = mock[ChapterStats]
    val stats = (topicStats, chapterStats)

    //then
    api.submitAnswer.expects(*, *, *).onCall { (t, c, d) =>
      t shouldBe topic
      c shouldBe chapter
      d shouldBe data
      Future.successful(respData)
    }
    statsService.updateStats.expects(*, *).onCall { (t, c) =>
      t shouldBe topic
      c shouldBe chapter
      Future.successful(stats)
    }
    dispatch.expects(QuestionFetchedAction(topic, chapter, respData))
    dispatch.expects(StatsFetchedAction(stats))

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

  private class QuestionActionsTest(api: QuestionApi, stats: StatsService)
    extends QuestionActions {

    protected def client: QuestionApi = api
    protected def statsService: StatsService = stats
  }
}
