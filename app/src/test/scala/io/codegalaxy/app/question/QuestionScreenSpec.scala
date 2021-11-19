package io.codegalaxy.app.question

import io.codegalaxy.api.question._
import io.codegalaxy.app.question.QuestionActions._
import io.codegalaxy.app.question.QuestionScreen._
import io.codegalaxy.app.topic.TopicParams
import org.scalatest.{Assertion, Succeeded}
import scommons.nodejs.test.AsyncTestSpec
import scommons.react.navigation._
import scommons.react.redux.Dispatch
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
  with TestRendererUtils {

  QuestionScreen.choiceGroupComp = mockUiComponent("ChoiceGroup")
  QuestionScreen.questionTextComp = mockUiComponent("QuestionText")
  QuestionScreen.questionButtonComp = mockUiComponent("QuestionButton")
  QuestionScreen.questionAnswerComp = mockUiComponent("QuestionAnswer")
  QuestionScreen.questionAnswerIcon = mockUiComponent("QuestionAnswerIcon")
  QuestionScreen.questionRuleComp = mockUiComponent("QuestionRule")

  it should "update selectedIds if un-answered when onSelectChange" in {
    //given
    val props = getQuestionScreenProps()
    val renderer = createTestRenderer(<(QuestionScreen())(^.wrapped := props)())
    val choiceComp = findComponentProps(renderer.root, choiceGroupComp)
    choiceComp.selectedIds shouldBe Set.empty

    //when
    choiceComp.onSelectChange(Set(1, 2))
    
    //then
    inside(findComponentProps(renderer.root, choiceGroupComp)) { case choice =>
      choice.selectedIds shouldBe Set(1, 2)
    }
  }

  it should "not update selectedIds if answered when onSelectChange" in {
    //given
    val props = {
      val props = getQuestionScreenProps()
      val question = inside(props.data.question) {
        case Some(question) => question
      }
      props.copy(data = props.data.copy(question = Some(question.copy(
        correct = Some(false),
        rules = Nil,
        explanation = None
      ))))
    }
    val renderer = createTestRenderer(<(QuestionScreen())(^.wrapped := props)())
    val choiceComp = findComponentProps(renderer.root, choiceGroupComp)
    choiceComp.selectedIds shouldBe Set.empty

    //when
    choiceComp.onSelectChange(Set(1, 2))
    
    //then
    inside(findComponentProps(renderer.root, choiceGroupComp)) { case choice =>
      choice.selectedIds shouldBe Set.empty
    }
  }

  it should "dispatch actions when onPress Continue button" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[QuestionActions]
    val props = getQuestionScreenProps(dispatch, actions)
    val renderer = createTestRenderer(<(QuestionScreen())(^.wrapped := props)())
    val choiceComp = findComponentProps(renderer.root, choiceGroupComp)
    val selectedChoiceId = 1
    choiceComp.onSelectChange(Set(selectedChoiceId))
    inside(findComponentProps(renderer.root, choiceGroupComp)) { case choice =>
      choice.selectedIds shouldBe Set(selectedChoiceId)
    }
    val buttonProps = findComponentProps(renderer.root, questionButtonComp)
    buttonProps.text shouldBe "Continue"
    val resp = mock[QuestionData]
    val topic = inside(props.data.topic) {
      case Some(topic) => topic
    }
    val chapter = inside(props.data.chapter) {
      case Some(chapter) => chapter
    }
    val submitAction = AnswerSubmitAction(FutureTask("Submitting...", Future.successful(resp)))
    val data = {
      val question = inside(props.data.question) {
        case Some(question) => question
      }
      question.copy(choices = question.choices.map { choice =>
        val selected = choice.id == selectedChoiceId
        choice.copy(selected = if (selected) Some(true) else Some(false))
      })
    }

    //then
    (actions.submitAnswer _).expects(dispatch, topic, chapter, data).returning(submitAction)
    dispatch.expects(submitAction)

    //when
    buttonProps.onPress()

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
    val renderer = createTestRenderer(<(QuestionScreen())(^.wrapped := props)())
    val choiceComp = findComponentProps(renderer.root, choiceGroupComp)
    choiceComp.onSelectChange(Set(1))
    inside(findComponentProps(renderer.root, choiceGroupComp)) { case choice =>
      choice.selectedIds shouldBe Set(1)
    }
    val updatedProps = {
      val question = inside(props.data.question) {
        case Some(question) => question
      }
      props.copy(data = props.data.copy(question = Some(question.copy(
        correct = Some(false),
        rules = Nil,
        explanation = None
      ))))
    }
    renderer.update(<(QuestionScreen())(^.wrapped := updatedProps)())
    val buttonProps = findComponentProps(renderer.root, questionButtonComp)
    buttonProps.text shouldBe "Next"
    val topic = inside(updatedProps.data.topic) {
      case Some(topic) => topic
    }
    val chapter = inside(updatedProps.data.chapter) {
      case Some(chapter) => chapter
    }
    val data = mock[QuestionData]
    val fetchAction = QuestionFetchAction(topic, chapter, FutureTask("Fetching...",
      Future.successful(data)))

    //then
    (actions.fetchQuestion _).expects(dispatch, topic, chapter).returning(fetchAction)
    dispatch.expects(fetchAction)

    //when
    buttonProps.onPress()

    //then
    fetchAction.task.future.map { _ =>
      inside(findComponentProps(renderer.root, choiceGroupComp)) { case choice =>
        choice.selectedIds shouldBe Set.empty
      }
    }
  }

  it should "dispatch actions if topic is changed when mount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[QuestionActions]
    val topic = "new_topic"
    val (props, chapter) = {
      val props = getQuestionScreenProps(dispatch, actions)
      val chapter = inside(props.data.chapter) {
        case Some(chapter) => chapter
      }
      (props.copy(params = props.params.copy(topic = topic)), chapter)
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
    val (props, topic) = {
      val props = getQuestionScreenProps(dispatch, actions)
      val topic = inside(props.data.topic) {
        case Some(topic) => topic
      }
      (props.copy(params = props.params.copy(chapter = Some(chapter))), topic)
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
    val result = testRender(<(QuestionScreen())(^.wrapped := props)())

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
    val result = testRender(<(QuestionScreen())(^.wrapped := props)())

    //then
    assertQuestionScreen(result, props)
  }

  it should "render answered incorrect question data" in {
    //given
    val props = {
      val props = getQuestionScreenProps()
      val question = inside(props.data.question) {
        case Some(question) => question
      }
      props.copy(data = props.data.copy(question = Some(question.copy(
        correct = Some(false),
        rules = List(RuleData("test rule title", "test rule text")),
        explanation = Some("test explanation")
      ))))
    }
    
    //when
    val result = testRender(<(QuestionScreen())(^.wrapped := props)())

    //then
    assertQuestionScreen(result, props)
  }

  it should "render answered correct question data" in {
    //given
    val props = {
      val props = getQuestionScreenProps()
      val question = inside(props.data.question) {
        case Some(question) => question
      }
      props.copy(data = props.data.copy(question = Some(question.copy(
        choices = question.choices.map(_.copy(correct = Some(true))),
        correct = Some(true),
        rules = Nil,
        explanation = None
      ))))
    }
    
    //when
    val result = testRender(<(QuestionScreen())(^.wrapped := props)())

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
  
  private def assertQuestionScreen(result: TestInstance, props: QuestionScreenProps): Assertion = {
    implicit val theme: Theme = DefaultTheme
    val question = inside(props.data.question) {
      case Some(question) => question
    }
    val answered = question.correct.isDefined
    
    def assertComponents(questionText: TestInstance,
                         choice: TestInstance,
                         button: TestInstance,
                         answerStatus: Option[TestInstance],
                         ruleComp: Option[TestInstance],
                         explanationComp: Option[TestInstance]): Assertion = {

      assertTestComponent(questionText, questionTextComp) { case QuestionTextProps(html, style) =>
        html shouldBe question.text
        style shouldBe None
      }

      assertTestComponent(choice, choiceGroupComp) {
        case ChoiceGroupProps(items, keyExtractor, iconRenderer, labelRenderer, selectedIds, _, multiSelect, style) =>
          items shouldBe question.choices
          keyExtractor(question.choices.head) shouldBe 1
          
          if (!answered) iconRenderer(false, theme) should not be null
          else iconRenderer(false, theme) shouldBe null

          val data = items.head
          val labelComp = createTestRenderer(labelRenderer(data, theme)).root
          val (maybeIcon, choiceLabel) =
            if (!answered) (None, labelComp)
            else inside(labelComp.children.toList) {
              case List(icon, label) => (Some(icon), label)
            }
          
          maybeIcon.foreach { icon =>
            assertTestComponent(icon, questionAnswerIcon) { case QuestionAnswerIconProps(correct) =>
              correct shouldBe data.correct.getOrElse(false)
            }
          }
          assertTestComponent(choiceLabel, questionTextComp) {
            case QuestionTextProps(textHtml, labelStyle) =>
              textHtml shouldBe data.choiceText
              labelStyle.get.toList shouldBe {
                if (answered) List(styles.choiceLabel, styles.choiceNotSelected)
                else List(styles.choiceLabel)
              }
          }

          selectedIds shouldBe Set.empty
          multiSelect shouldBe false
          style shouldBe Some(styles.choiceGroup)
      }

      answerStatus.foreach { status =>
        assertTestComponent(status, questionAnswerComp) { case QuestionAnswerProps(correct) =>
          correct shouldBe question.correct.getOrElse(false)
        }
      }
      
      ruleComp.foreach { comp =>
        val rule = question.rules.head
        
        assertTestComponent(comp, questionRuleComp) {
          case QuestionRuleProps(resTitle, resText) =>
            resTitle shouldBe rule.title
            resText shouldBe rule.text
        }
      }
      
      explanationComp.foreach { comp =>
        assertTestComponent(comp, questionRuleComp) {
          case QuestionRuleProps(resTitle, resText) =>
            resTitle shouldBe "Explanation"
            Some(resText) shouldBe question.explanation
        }
      }
      
      assertTestComponent(button, questionButtonComp) { case QuestionButtonProps(text, _) =>
        text shouldBe (if (!answered) "Continue" else "Next")
      }
    }
    
    assertNativeComponent(result,
      <.SafeAreaView(
        ^.rnStyle := styles.container,
        ^.edges := List(SafeAreaEdge.left, SafeAreaEdge.bottom, SafeAreaEdge.right)
      )(),
      inside(_) { case List(scroll) =>
        assertNativeComponent(scroll,
          <.ScrollView(
            ^.keyboardShouldPersistTaps := KeyboardShouldPersistTaps.always
          )(),
          inside(_) {
            case List(text, choice, continue) if !answered =>
              assertComponents(text, choice, continue, None, None, None)
            case List(text, choice, status, next) if {
              question.rules.isEmpty && question.explanation.isEmpty
            } =>
              assertComponents(text, choice, next, Some(status), None, None)
            case List(text, choice, status, ruleComp, explanationComp, next) if {
              question.rules.nonEmpty && question.explanation.nonEmpty
            } =>
              assertComponents(text, choice, next, Some(status), Some(ruleComp), Some(explanationComp))
          }
        )
      })
  }
}
