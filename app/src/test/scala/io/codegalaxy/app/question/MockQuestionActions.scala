package io.codegalaxy.app.question

import io.codegalaxy.api.question._
import io.codegalaxy.app.question.QuestionActions._
import io.codegalaxy.app.stats.StatsService
import scommons.react.redux.Dispatch

//noinspection NotImplementedCode
class MockQuestionActions(
  fetchQuestionMock: (Dispatch, String, String) => QuestionFetchAction = (_, _, _) => ???,
  submitAnswerMock: (Dispatch, String, String, QuestionData) => AnswerSubmitAction = (_, _, _, _) => ???
) extends QuestionActions {

  override protected def client: QuestionApi = ???
  override protected def statsService: StatsService = ???
  
  override def fetchQuestion(dispatch: Dispatch, topic: String, chapter: String): QuestionFetchAction =
    fetchQuestionMock(dispatch, topic, chapter)
    
  override def submitAnswer(dispatch: Dispatch, topic: String, chapter: String, data: QuestionData): AnswerSubmitAction =
    submitAnswerMock(dispatch, topic, chapter, data)
}
