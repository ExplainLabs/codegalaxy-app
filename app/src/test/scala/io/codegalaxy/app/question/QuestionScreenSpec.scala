package io.codegalaxy.app.question

import io.codegalaxy.api.question._
import io.codegalaxy.app.question.QuestionActions._
import io.codegalaxy.app.question.QuestionScreen._
import io.codegalaxy.app.topic.TopicParams
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import org.scalatest.{Assertion, Succeeded}
import scommons.expo._
import scommons.nodejs.test.AsyncTestSpec
import scommons.react._
import scommons.react.navigation._
import scommons.react.redux.task.FutureTask
import scommons.react.test._
import scommons.reactnative.ScrollView._
import scommons.reactnative._
import scommons.reactnative.safearea.SafeArea._
import scommons.reactnative.safearea._
import scommons.reactnative.ui._

import scala.concurrent.Future

class QuestionScreenSpec extends AsyncTestSpec
  with BaseTestSpec
  with ShallowRendererUtils
  with TestRendererUtils {

  it should "update selectedIds if un-answered when onSelectChange" in {
    //given
    val props = getQuestionScreenProps()
    val renderer = createRenderer()
    renderer.render(<(QuestionScreen())(^.wrapped := props)())
    val choiceComp = findComponentProps(renderer.getRenderOutput(), choiceGroupComp)
    choiceComp.selectedIds shouldBe Set.empty

    //when
    choiceComp.onSelectChange(Set(1, 2))
    
    //then
    inside(findComponentProps(renderer.getRenderOutput(), choiceGroupComp)) { case choice =>
      choice.selectedIds shouldBe Set(1, 2)
    }
  }

  it should "not update selectedIds if answered when onSelectChange" in {
    //given
    val props = {
      val props = getQuestionScreenProps()
      val Some(question) = props.data.question
      props.copy(data = props.data.copy(question = Some(question.copy(
        correct = Some(false),
        rules = Nil,
        explanation = None
      ))))
    }
    val renderer = createRenderer()
    renderer.render(<(QuestionScreen())(^.wrapped := props)())
    val choiceComp = findComponentProps(renderer.getRenderOutput(), choiceGroupComp)
    choiceComp.selectedIds shouldBe Set.empty

    //when
    choiceComp.onSelectChange(Set(1, 2))
    
    //then
    inside(findComponentProps(renderer.getRenderOutput(), choiceGroupComp)) { case choice =>
      choice.selectedIds shouldBe Set.empty
    }
  }

  it should "dispatch actions when onPress Continue button" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[QuestionActions]
    val props = getQuestionScreenProps(dispatch, actions)
    val renderer = createRenderer()
    renderer.render(<(QuestionScreen())(^.wrapped := props)())
    val choiceComp = findComponentProps(renderer.getRenderOutput(), choiceGroupComp)
    val selectedChoiceId = 1
    choiceComp.onSelectChange(Set(selectedChoiceId))
    inside(findComponentProps(renderer.getRenderOutput(), choiceGroupComp)) { case choice =>
      choice.selectedIds shouldBe Set(selectedChoiceId)
    }
    val List(button) = findComponents(renderer.getRenderOutput(), <.TouchableOpacity.reactClass)
    val resp = mock[QuestionData]
    val Some(topic) = props.data.topic
    val Some(chapter) = props.data.chapter
    val submitAction = AnswerSubmitAction(FutureTask("Submitting...", Future.successful(resp)))
    val data = {
      val Some(question) = props.data.question
      question.copy(choices = question.choices.map { choice =>
        val selected = choice.id == selectedChoiceId
        choice.copy(selected = if (selected) Some(true) else Some(false))
      })
    }

    //then
    (actions.submitAnswer _).expects(dispatch, topic, chapter, data).returning(submitAction)
    dispatch.expects(submitAction)

    //when
    button.props.onPress()

    //then
    submitAction.task.future.map { _ =>
      Succeeded
    }
  }

  it should "dispatch actions when onPress Next button" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[QuestionActions]
    val props = getQuestionScreenProps(dispatch, actions)
    val renderer = createRenderer()
    renderer.render(<(QuestionScreen())(^.wrapped := props)())
    val choiceComp = findComponentProps(renderer.getRenderOutput(), choiceGroupComp)
    choiceComp.onSelectChange(Set(1))
    inside(findComponentProps(renderer.getRenderOutput(), choiceGroupComp)) { case choice =>
      choice.selectedIds shouldBe Set(1)
    }
    val updatedProps = {
      val Some(question) = props.data.question
      props.copy(data = props.data.copy(question = Some(question.copy(
        correct = Some(false),
        rules = Nil,
        explanation = None
      ))))
    }
    renderer.render(<(QuestionScreen())(^.wrapped := updatedProps)())
    val List(button) = findComponents(renderer.getRenderOutput(), <.TouchableOpacity.reactClass)
    val Some(topic) = updatedProps.data.topic
    val Some(chapter) = updatedProps.data.chapter
    val data = mock[QuestionData]
    val fetchAction = QuestionFetchAction(topic, chapter, FutureTask("Fetching...",
      Future.successful(data)))

    //then
    (actions.fetchQuestion _).expects(dispatch, topic, chapter).returning(fetchAction)
    dispatch.expects(fetchAction)

    //when
    button.props.onPress()

    //then
    fetchAction.task.future.map { _ =>
      inside(findComponentProps(renderer.getRenderOutput(), choiceGroupComp)) { case choice =>
        choice.selectedIds shouldBe Set.empty
      }
    }
  }

  it should "dispatch actions if topic is changed when mount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[QuestionActions]
    val topic = "new_topic"
    val (props, Some(chapter)) = {
      val props = getQuestionScreenProps(dispatch, actions)
      (props.copy(params = props.params.copy(topic = topic)), props.data.chapter)
    }
    val data = mock[QuestionData]
    val fetchAction = QuestionFetchAction(topic, chapter, FutureTask("Fetching Question",
      Future.successful(data)))
    
    //then
    (actions.fetchQuestion _).expects(dispatch, topic, chapter).returning(fetchAction)
    dispatch.expects(fetchAction)

    //when
    testRender(<(QuestionScreen())(^.wrapped := props)())

    //then
    fetchAction.task.future.map { _ =>
      Succeeded
    }
  }

  it should "dispatch actions if chapter is changed when mount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[QuestionActions]
    val chapter = "new_chapter"
    val (props, Some(topic)) = {
      val props = getQuestionScreenProps(dispatch, actions)
      (props.copy(params = props.params.copy(chapter = Some(chapter))), props.data.topic)
    }
    val data = mock[QuestionData]
    val fetchAction = QuestionFetchAction(topic, chapter, FutureTask("Fetching Question",
      Future.successful(data)))
    
    //then
    (actions.fetchQuestion _).expects(dispatch, topic, chapter).returning(fetchAction)
    dispatch.expects(fetchAction)

    //when
    testRender(<(QuestionScreen())(^.wrapped := props)())

    //then
    fetchAction.task.future.map { _ =>
      Succeeded
    }
  }

  it should "do not dispatch actions if params not changed when mount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[QuestionActions]
    val props = getQuestionScreenProps(dispatch, actions)
    
    //then
    (actions.fetchQuestion _).expects(*, *, *).never()

    //when
    testRender(<(QuestionScreen())(^.wrapped := props)())

    Succeeded
  }

  it should "render Loading... if no question data" in {
    //given
    val props =  {
      val props = getQuestionScreenProps()
      props.copy(data = props.data.copy(question = None))
    }
    
    //when
    val result = shallowRender(<(QuestionScreen())(^.wrapped := props)())

    //then
    implicit val theme: Theme = DefaultTheme
    assertNativeComponent(result,
      <.SafeAreaView(
        ^.rnStyle := styles.container,
        ^.edges := List(SafeAreaEdge.left, SafeAreaEdge.bottom, SafeAreaEdge.right)
      )(
        <.Text(^.rnStyle := themeTextStyle)("Loading...")
      )
    )
  }

  it should "render un-answered question data" in {
    //given
    val props = getQuestionScreenProps()
    
    //when
    val result = shallowRender(<(QuestionScreen())(^.wrapped := props)())

    //then
    assertQuestionScreen(result, props)
  }

  it should "render answered incorrect question data" in {
    //given
    val props = {
      val props = getQuestionScreenProps()
      val Some(question) = props.data.question
      props.copy(data = props.data.copy(question = Some(question.copy(
        correct = Some(false),
        rules = List(RuleData("test rule title", "test rule text")),
        explanation = Some("test explanation")
      ))))
    }
    
    //when
    val result = shallowRender(<(QuestionScreen())(^.wrapped := props)())

    //then
    assertQuestionScreen(result, props)
  }

  it should "render answered correct question data" in {
    //given
    val props = {
      val props = getQuestionScreenProps()
      val Some(question) = props.data.question
      props.copy(data = props.data.copy(question = Some(question.copy(
        choices = question.choices.map(_.copy(correct = Some(true))),
        correct = Some(true),
        rules = Nil,
        explanation = None
      ))))
    }
    
    //when
    val result = shallowRender(<(QuestionScreen())(^.wrapped := props)())

    //then
    assertQuestionScreen(result, props)
  }

  private def getQuestionScreenProps(dispatch: Dispatch = mock[Dispatch],
                                     actions: QuestionActions = mock[QuestionActions],
                                     data: QuestionState = QuestionState(
                                       topic = Some("test_topic"),
                                       chapter = Some("test_chapter"),
                                       question = Some(QuestionData(
                                         uuid = "14h15kl1h514l5h4315j145lj1",
                                         text = "Can methods, taking one argument, be used with infix syntax?",
                                         answerType = "SINGLE_CHOICE",
                                         choices = List(
                                           ChoiceData(
                                             id = 1,
                                             choiceText = "Yes"
                                           ),
                                           ChoiceData(
                                             id = 2,
                                             choiceText = "No"
                                           )
                                         )
                                       ))
                                     ),
                                     params: TopicParams = TopicParams("test_topic", Some("test_chapter"))
                                    ): QuestionScreenProps = {
    QuestionScreenProps(
      dispatch = dispatch,
      actions = actions,
      data = data,
      params = params
    )
  }
  
  private def assertQuestionScreen(result: ShallowInstance, props: QuestionScreenProps): Assertion = {
    implicit val theme: Theme = DefaultTheme
    val Some(question) = props.data.question
    val answered = question.correct.isDefined
    
    def assertComponents(questionText: ShallowInstance,
                         choice: ShallowInstance,
                         button: ShallowInstance,
                         answerStatus: Option[ShallowInstance],
                         rule: Option[ShallowInstance],
                         explanation: Option[ShallowInstance]): Assertion = {

      assertComponent(questionText, QuestionText) { case QuestionTextProps(html, style) =>
        html shouldBe question.text
        style shouldBe None
      }

      assertComponent(choice, choiceGroupComp) {
        case ChoiceGroupProps(items, keyExtractor, iconRenderer, labelRenderer, selectedIds, _, multiSelect, style) =>
          items shouldBe question.choices
          keyExtractor(question.choices.head) shouldBe 1
          
          if (!answered) iconRenderer(false, theme) should not be null
          else iconRenderer(false, theme) shouldBe null

          val data = items.head
          assertNativeComponent(wrapAndRender(labelRenderer(data, theme)), <.>()(), { children: List[ShallowInstance] =>
            val (maybeIcon, choiceLabel) = children match {
              case List(label) if !answered => (None, label)
              case List(icon, label) => (Some(icon), label)
            }
            val correct = data.correct.getOrElse(false)
            maybeIcon.foreach { icon =>
              assertNativeComponent(icon,
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
              )
            }
            assertComponent(choiceLabel, QuestionText) {
              case QuestionTextProps(textHtml, labelStyle) =>
                textHtml shouldBe data.choiceText
                labelStyle.get.toList shouldBe {
                  if (answered) List(styles.choiceLabel, styles.choiceNotSelected)
                  else List(styles.choiceLabel)
                }
            }
          })

          selectedIds shouldBe Set.empty
          multiSelect shouldBe false
          style shouldBe Some(styles.choiceGroup)
      }

      answerStatus.foreach { status =>
        if (!question.correct.getOrElse(false)) {
          assertNativeComponent(status,
            <.Text(^.rnStyle := styles.wrongAnswer)("Oops! This is the wrong answer.")
          )
        }
        else {
          assertNativeComponent(status,
            <.Text(^.rnStyle := styles.rightAnswer)("Well done! Right answer.")
          )
        }
      }
      
      rule.foreach { ruleComp =>
        val rule = question.rules.head
        
        assertNativeComponent(ruleComp, <.>()(), { children: List[ShallowInstance] =>
          val List(title, text) = children
          assertNativeComponent(title,
            <.Text(themeStyle(styles.ruleTitle, themeTextStyle))(rule.title)
          )
          assertComponent(text, QuestionText) {
            case QuestionTextProps(textHtml, labelStyle) =>
              textHtml shouldBe rule.text
              labelStyle.get.toList shouldBe List(styles.ruleText)
          }
        })
      }
      
      explanation.foreach { explanationComp =>
        val Some(explanation) = question.explanation
        
        assertNativeComponent(explanationComp, <.>()(), { children: List[ShallowInstance] =>
          val List(title, text) = children
          assertNativeComponent(title,
            <.Text(themeStyle(styles.ruleTitle, themeTextStyle))("Explanation")
          )
          assertComponent(text, QuestionText) {
            case QuestionTextProps(textHtml, labelStyle) =>
              textHtml shouldBe explanation
              labelStyle.get.toList shouldBe List(styles.ruleText)
          }
        })
      }
      
      assertNativeComponent(button,
        <.TouchableOpacity(^.rnStyle := styles.button)(
          <.Text(^.rnStyle := styles.buttonText)(
            if (!answered) "Continue"
            else "Next"
          ),
          <(VectorIcons.Ionicons)(
            ^.name := "ios-arrow-forward",
            ^.rnSize := 24,
            ^.color := Style.Color.dodgerblue
          )()
        )
      )
    }
    
    assertNativeComponent(result,
      <.SafeAreaView(
        ^.rnStyle := styles.container,
        ^.edges := List(SafeAreaEdge.left, SafeAreaEdge.bottom, SafeAreaEdge.right)
      )(),
      { children: List[ShallowInstance] =>
        val List(scroll) = children
        assertNativeComponent(scroll,
          <.ScrollView(
            ^.keyboardShouldPersistTaps := KeyboardShouldPersistTaps.always
          )(),
          { children: List[ShallowInstance] =>
            children match {
              case List(text, choice, continue) if !answered =>
                assertComponents(text, choice, continue, None, None, None)
              case List(text, choice, status, next) if {
                question.rules.isEmpty && question.explanation.isEmpty
              } =>
                assertComponents(text, choice, next, Some(status), None, None)
              case List(text, choice, status, rule, explanation, next) if {
                question.rules.nonEmpty && question.explanation.nonEmpty
              } =>
                assertComponents(text, choice, next, Some(status), Some(rule), Some(explanation))
            }
          }
        )
      })
  }

  private def wrapAndRender(element: ReactElement): ShallowInstance = {
    val wrapper = new FunctionComponent[Unit] {
      protected def render(props: Props): ReactElement = {
        element
      }
    }.apply()

    shallowRender(<(wrapper)()())
  }
}
