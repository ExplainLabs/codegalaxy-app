package io.codegalaxy.app.question

import io.codegalaxy.app.topic.TopicParams
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react._
import scommons.react.hooks._
import scommons.reactnative._

case class QuestionScreenProps(dispatch: Dispatch,
                               actions: QuestionActions,
                               data: QuestionState,
                               params: TopicParams)

object QuestionScreen extends FunctionComponent[QuestionScreenProps] {

  protected def render(compProps: Props): ReactElement = {
    val props = compProps.wrapped
    val topic = props.params.topic
    val chapter = props.params.getChapter
    
    useEffect({ () =>
      if (!props.data.topic.contains(topic) || !props.data.chapter.contains(chapter)) {
        props.dispatch(props.actions.fetchQuestion(props.dispatch, topic, chapter))
      }
      ()
    }, Nil)

    props.data.question match {
      case None => <.Text()("Loading...")
      case Some(question) =>
        <.Text()(
          s"""text: ${question.text}
             |
             |answerType: ${question.answerType}
             |
             |choices: ${question.choices.mkString("\n")}
             |""".stripMargin
        )
    }
  }
}
