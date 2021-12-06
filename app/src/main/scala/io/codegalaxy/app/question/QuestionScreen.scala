package io.codegalaxy.app.question

import io.codegalaxy.app.topic.TopicParams
import scommons.react._
import scommons.react.hooks._
import scommons.react.navigation._
import scommons.react.redux.Dispatch
import scommons.reactnative.ScrollView._
import scommons.reactnative._
import scommons.reactnative.safearea.SafeArea._
import scommons.reactnative.safearea._

import scala.scalajs.js

case class QuestionScreenProps(dispatch: Dispatch,
                               actions: QuestionActions,
                               data: QuestionState,
                               params: TopicParams)

object QuestionScreen extends FunctionComponent[QuestionScreenProps] {

  private[question] var questionViewComp: UiComponent[QuestionViewProps] = QuestionView
  
  protected def render(compProps: Props): ReactElement = {
    implicit val theme: Theme = useTheme()
    val props = compProps.wrapped
    val topic = props.params.topic
    val chapter = props.params.getChapter
    
    useEffect({ () =>
      if (!props.data.topic.contains(topic) || !props.data.chapter.contains(chapter)) {
        props.dispatch(props.actions.fetchQuestion(props.dispatch, topic, chapter))
      }
      ()
    }, Nil)

    <.SafeAreaView(
      ^.rnStyle := styles.container,
      ^.edges := List(SafeAreaEdge.left, SafeAreaEdge.bottom, SafeAreaEdge.right)
    )(props.data.question match {
      case None => <.Text(^.rnStyle := themeTextStyle)("Loading...")
      case Some(question) =>
        <.ScrollView(^.keyboardShouldPersistTaps := KeyboardShouldPersistTaps.always)(
          <(questionViewComp())(^.wrapped := QuestionViewProps(
            data = question,
            onSubmitAnswer = { data =>
              props.dispatch(props.actions.submitAnswer(props.dispatch, topic, chapter, data))
            },
            onNextQuestion = { () =>
              props.dispatch(props.actions.fetchQuestion(props.dispatch, topic, chapter))
            }
          ))()
        )
    })
  }

  private[question] lazy val styles = StyleSheet.create(new Styles)
  private[question] class Styles extends js.Object {

    val container: Style = new ViewStyle {
      override val flex = 1
      override val margin = 5
      override val padding = 5
      override val marginBottom = 0
      override val paddingBottom = 0
    }
  }
}
