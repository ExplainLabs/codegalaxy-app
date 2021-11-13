package io.codegalaxy.app.question

import io.codegalaxy.api.question.ChoiceData
import io.codegalaxy.app.topic.TopicParams
import scommons.expo._
import scommons.react._
import scommons.react.hooks._
import scommons.react.navigation._
import scommons.react.redux.Dispatch
import scommons.reactnative.ScrollView._
import scommons.reactnative._
import scommons.reactnative.safearea.SafeArea._
import scommons.reactnative.safearea._
import scommons.reactnative.ui._

import scala.scalajs.js

case class QuestionScreenProps(dispatch: Dispatch,
                               actions: QuestionActions,
                               data: QuestionState,
                               params: TopicParams)

object QuestionScreen extends FunctionComponent[QuestionScreenProps] {

  private[question] var choiceGroupComp: UiComponent[ChoiceGroupProps[Int, ChoiceData]] =
    new ChoiceGroup[Int, ChoiceData]
  private[question] var questionTextComp: UiComponent[QuestionTextProps] = QuestionText
  
  private def renderButton(text: String, onPress: js.Function0[Unit]): ReactElement = {
    <.TouchableOpacity(
      ^.rnStyle := styles.button,
      ^.onPress := onPress
    )(
      <.Text(^.rnStyle := styles.buttonText)(text),
      <(VectorIcons.Ionicons)(
        ^.name := "ios-arrow-forward",
        ^.rnSize := 24,
        ^.color := Style.Color.dodgerblue
      )()
    )
  }
  
  private def renderAnswerIcon(correct: Boolean): ReactElement = {
    <(VectorIcons.Ionicons)(
      ^.name := {
        if (correct) "ios-checkmark"
        else "ios-close"
      },
      ^.rnSize := 24,
      ^.color := {
        if (correct) Style.Color.green
        else Style.Color.red
      }
    )()
  }
  
  protected def render(compProps: Props): ReactElement = {
    implicit val theme: Theme = useTheme()
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

    <.SafeAreaView(
      ^.rnStyle := styles.container,
      ^.edges := List(SafeAreaEdge.left, SafeAreaEdge.bottom, SafeAreaEdge.right)
    )(props.data.question match {
      case None => <.Text(^.rnStyle := themeTextStyle)("Loading...")
      case Some(question) =>
        val multiSelect = question.answerType != "SINGLE_CHOICE"
        val answered = question.correct.isDefined
        
        <.ScrollView(^.keyboardShouldPersistTaps := KeyboardShouldPersistTaps.always)(
          <(questionTextComp())(^.wrapped := QuestionTextProps(question.text))(),

          <(choiceGroupComp())(^.wrapped := new ChoiceGroupProps[Int, ChoiceData](
            items = question.choices,
            keyExtractor = _.id,
            iconRenderer = {
              if (!answered) ChoiceGroupProps.defaultIconRenderer(multiSelect)
              else { (_, _) => null }
            },
            labelRenderer = { (data, _) =>
              val selected = selectedIds.contains(data.id)
              <.>()(
                if (answered) Some(renderAnswerIcon(data.correct.getOrElse(false)))
                else None,
                <(questionTextComp())(^.wrapped := QuestionTextProps(
                  textHtml = data.choiceText,
                  style = Some(
                    if (answered && !selected) js.Array(styles.choiceLabel, styles.choiceNotSelected)
                    else js.Array(styles.choiceLabel)
                  )
                ))()
              )
            },
            selectedIds = selectedIds,
            onSelectChange = { ids =>
              if (!answered) {
                setSelectedIds(ids)
              }
            },
            multiSelect = multiSelect,
            style = Some(styles.choiceGroup)
          ))(),

          question.correct.map {
            case false => <.Text(^.rnStyle := styles.wrongAnswer)("Oops! This is the wrong answer.")
            case true => <.Text(^.rnStyle := styles.rightAnswer)("Well done! Right answer.")
          },

          question.rules.map { rule =>
            <.>()(
              <.Text(themeStyle(styles.ruleTitle, themeTextStyle))(rule.title),
              <(questionTextComp())(^.wrapped := QuestionTextProps(
                textHtml = rule.text,
                style = Some(js.Array(styles.ruleText))
              ))()
            )
          },

          question.explanation.collect { case explanation if explanation.trim.nonEmpty =>
            <.>()(
              <.Text(themeStyle(styles.ruleTitle, themeTextStyle))("Explanation"),
              <(questionTextComp())(^.wrapped := QuestionTextProps(
                textHtml = explanation,
                style = Some(js.Array(styles.ruleText))
              ))()
            )
          },

          if (!answered) {
            renderButton("Continue", { () =>
              val data = question.copy(choices = question.choices.map { choice =>
                val selected = selectedIds.contains(choice.id)
                choice.copy(selected = if (selected) Some(true) else Some(false))
              })
              props.dispatch(props.actions.submitAnswer(props.dispatch, topic, chapter, data))
            })
          }
          else {
            renderButton("Next", { () =>
              props.dispatch(props.actions.fetchQuestion(props.dispatch, topic, chapter))
              setSelectedIds(Set.empty)
            })
          }
        )
    })
  }

  private[question] lazy val styles = StyleSheet.create(new Styles)
  private[question] class Styles extends js.Object {
    import TextStyle._
    import ViewStyle._

    val container: Style = new ViewStyle {
      override val flex = 1
      override val margin = 5
      override val padding = 5
      override val marginBottom = 0
      override val paddingBottom = 0
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
    val choiceNotSelected = new ViewStyle {
      val opacity = 0.5
    }
    val rightAnswer: Style = new TextStyle {
      override val marginVertical = 5
      override val textAlign = TextAlign.center
      override val fontWeight = FontWeight.bold
      override val color = Style.Color.green
      override val backgroundColor = Style.Color.lightcyan
    }
    val wrongAnswer: Style = new TextStyle {
      override val marginVertical = 5
      override val textAlign = TextAlign.center
      override val fontWeight = FontWeight.bold
      override val color = Style.Color.red
      override val backgroundColor = Style.Color.lightpink
    }
    val ruleTitle: Style = new TextStyle {
      override val textAlign = TextAlign.center
      override val fontWeight = FontWeight.bold
      override val marginVertical = 5
    }
    val ruleText: Style = new ViewStyle {
      override val marginVertical = 5
    }
    val button: Style = new ViewStyle {
      override val alignSelf = AlignSelf.center
      override val flexDirection = FlexDirection.row
      override val alignItems = AlignItems.center
      override val marginVertical = 10
    }
    val buttonText: Style = new TextStyle {
      override val fontWeight = FontWeight.bold
      override val fontSize = 18
      override val color = Style.Color.dodgerblue
      override val marginRight = 5
    }
  }
}
