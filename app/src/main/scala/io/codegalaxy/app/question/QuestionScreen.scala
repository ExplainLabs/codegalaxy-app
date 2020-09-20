package io.codegalaxy.app.question

import io.codegalaxy.api.question.ChoiceData
import io.codegalaxy.app.topic.TopicParams
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react._
import scommons.react.hooks._
import scommons.reactnative.ScrollView._
import scommons.reactnative._
import scommons.reactnative.ui._

import scala.scalajs.js

case class QuestionScreenProps(dispatch: Dispatch,
                               actions: QuestionActions,
                               data: QuestionState,
                               params: TopicParams)

object QuestionScreen extends FunctionComponent[QuestionScreenProps] {

  private[question] lazy val choiceGroupComp = new ChoiceGroup[Int, ChoiceData]
  
  protected def render(compProps: Props): ReactElement = {
    val props = compProps.wrapped
    val topic = props.params.topic
    val chapter = props.params.getChapter
    val (selectedIds, setSelectedIds) = useState(Set.empty[Int])
    
    useEffect({ () =>
      if (!props.data.topic.contains(topic) || !props.data.chapter.contains(chapter)) {
        props.dispatch(props.actions.fetchQuestion(props.dispatch, topic, chapter))
      }
      ()
    }, Nil)

    props.data.question match {
      case None => <.Text()("Loading...")
      case Some(question) =>
        val multiSelect = question.answerType != "SINGLE_CHOICE"
        
        <.ScrollView(
          ^.rnStyle := styles.container,
          ^.keyboardShouldPersistTaps := KeyboardShouldPersistTaps.always
        )(
          <(QuestionText())(^.wrapped := QuestionTextProps(question.text))(),

          <(choiceGroupComp())(^.wrapped := new ChoiceGroupProps[Int, ChoiceData](
            items = question.choices,
            keyExtractor = _.id,
            iconRenderer = ChoiceGroupProps.defaultIconRenderer(multiSelect),
            labelRenderer = { data =>
              <(QuestionText())(^.wrapped := QuestionTextProps(
                textHtml = data.choiceText,
                style = Some(styles.choiceLabel)
              ))()
            },
            selectedIds = selectedIds,
            onSelectChange = setSelectedIds,
            multiSelect = multiSelect,
            style = Some(styles.choiceGroup)
          ))(),

          <.Text()(
            s"""
               |text: ${question.text}
               |
               |answerType: ${question.answerType}
               |
               |choices: ${question.choices.mkString("\n")}
               |""".stripMargin
          )
        )
    }
  }

  private[question] lazy val styles = StyleSheet.create(new Styles)
  private[question] class Styles extends js.Object {
    import ViewStyle._

    val container: Style = new ViewStyle {
      override val margin = 5
      override val padding = 5
    }
    val choiceGroup: Style = new ViewStyle {
      override val alignSelf = AlignSelf.center
      override val margin = 5
      override val paddingVertical = 5
      override val paddingLeft = 5
      override val paddingRight = 25
    }
    val choiceLabel: Style = new ViewStyle {
      override val marginHorizontal = 5
      override val paddingHorizontal = 5
      override val borderBottomWidth = 1
      override val borderBottomColor = Style.Color.gray
    }
  }
}
