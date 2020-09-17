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

  it should "update state if topic is changed when QuestionFetchAction" in {
    //given
    val topic = "new_topic"
    val chapter = "test_chapter"
    val data = mock[QuestionData]
    val state = QuestionState(
      topic = Some("test_topic"),
      chapter = Some(chapter),
      question = Some(data)
    )
    val task = FutureTask("Fetching...", Future.successful(data))

    //when & then
    reduce(Some(state), QuestionFetchAction(topic, chapter, task)) shouldBe QuestionState(
      topic = Some(topic),
      chapter = Some(chapter),
      question = None
    )
  }
  
  it should "update state if chapter is changed when QuestionFetchAction" in {
    //given
    val topic = "test_topic"
    val chapter = "new_chapter"
    val data = mock[QuestionData]
    val state = QuestionState(
      topic = Some(topic),
      chapter = Some("test_chapter"),
      question = Some(data)
    )
    val task = FutureTask("Fetching...", Future.successful(data))

    //when & then
    reduce(Some(state), QuestionFetchAction(topic, chapter, task)) shouldBe QuestionState(
      topic = Some(topic),
      chapter = Some(chapter),
      question = None
    )
  }
  
  it should "not update state if data is not changed when QuestionFetchAction" in {
    //given
    val topic = "test_topic"
    val chapter = "test_chapter"
    val data = mock[QuestionData]
    val state = QuestionState(
      topic = Some(topic),
      chapter = Some(chapter),
      question = Some(data)
    )
    val task = FutureTask("Fetching...", Future.successful(data))

    //when & then
    reduce(Some(state), QuestionFetchAction(topic, chapter, task)) shouldBe theSameInstanceAs(state)
  }
  
  it should "update state when QuestionFetchedAction" in {
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
