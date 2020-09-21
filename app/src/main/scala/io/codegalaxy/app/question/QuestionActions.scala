package io.codegalaxy.app.question

import io.codegalaxy.api.question.{QuestionApi, QuestionData}
import io.codegalaxy.app.question.QuestionActions._
import io.github.shogowada.scalajs.reactjs.redux.Action
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react.redux.task.{FutureTask, TaskAction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

trait QuestionActions {

  protected def client: QuestionApi

  def fetchQuestion(dispatch: Dispatch, topic: String, chapter: String): QuestionFetchAction = {
    val resultF = client.getNextQuestion(topic, chapter).andThen {
      case Success(data) => dispatch(QuestionFetchedAction(topic, chapter, data))
    }

    QuestionFetchAction(topic, chapter, FutureTask("Fetching Question", resultF))
  }
  
  def submitAnswer(dispatch: Dispatch, topic: String, chapter: String, data: QuestionData): AnswerSubmitAction = {
    val resultF = client.submitAnswer(topic, chapter, data).andThen {
      case Success(resp) => dispatch(QuestionFetchedAction(topic, chapter, resp))
    }

    AnswerSubmitAction(FutureTask("Submitting Answer", resultF))
  }
}

object QuestionActions {

  case class QuestionFetchAction(topic: String, chapter: String, task: FutureTask[QuestionData]) extends TaskAction
  case class QuestionFetchedAction(topic: String, chapter: String, data: QuestionData) extends Action
  
  case class AnswerSubmitAction(task: FutureTask[QuestionData]) extends TaskAction
}
