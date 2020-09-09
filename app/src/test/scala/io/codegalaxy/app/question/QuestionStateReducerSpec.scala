package io.codegalaxy.app.question

import io.codegalaxy.api.question.QuestionData
import io.codegalaxy.app.BaseStateReducerSpec
import io.codegalaxy.app.question.QuestionActions._
import scommons.react.redux.task.FutureTask

import scala.concurrent.Future

class QuestionStateReducerSpec extends BaseStateReducerSpec(
  createState = QuestionState(),
  reduce = QuestionStateReducer.apply
) {

  it should "set topic, chapter and reset question when QuestionFetchAction" in {
    //given
    val topic = "test_topic"
    val chapter = "test_chapter"
    val data = mock[QuestionData]
    val state = QuestionState(question = Some(data))
    val task = FutureTask("Fetching...", Future.successful(data))

    //when & then
    reduce(Some(state), QuestionFetchAction(topic, chapter, task)) shouldBe QuestionState(
      topic = Some(topic),
      chapter = Some(chapter),
      question = None
    )
  }
  
  it should "set topic, chapter and question when QuestionFetchedAction" in {
    //given
    val topic = "test_topic"
    val chapter = "test_chapter"
    val data = mock[QuestionData]

    //when & then
    reduce(Some(QuestionState()), QuestionFetchedAction(topic, chapter, data)) shouldBe QuestionState(
      topic = Some(topic),
      chapter = Some(chapter),
      question = Some(data)
    )
  }
}
