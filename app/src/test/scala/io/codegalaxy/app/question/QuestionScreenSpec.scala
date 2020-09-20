package io.codegalaxy.app.question

import io.codegalaxy.api.question._
import io.codegalaxy.app.question.QuestionActions._
import io.codegalaxy.app.question.QuestionScreen._
import io.codegalaxy.app.topic.TopicParams
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import org.scalatest.{Assertion, Succeeded}
import scommons.nodejs.test.AsyncTestSpec
import scommons.react._
import scommons.react.redux.task.FutureTask
import scommons.react.test._
import scommons.reactnative.ScrollView._
import scommons.reactnative._
import scommons.reactnative.ui._

import scala.concurrent.Future

class QuestionScreenSpec extends AsyncTestSpec
  with BaseTestSpec
  with ShallowRendererUtils
  with TestRendererUtils {

  it should "save selected choices when onSelectChange" in {
    //given
    val props = getQuestionScreenProps()
    val renderer = createRenderer()
    renderer.render(<(QuestionScreen())(^.wrapped := props)())
    val choiceComp = findComponentProps(renderer.getRenderOutput(), choiceGroupComp)

    //when
    choiceComp.onSelectChange(Set(1, 2))
    
    //then
    inside(findComponentProps(renderer.getRenderOutput(), choiceGroupComp)) { case choice =>
      choice.selectedIds shouldBe Set(1, 2)
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

  it should "render Loading... if no question data " in {
    //given
    val props =  {
      val props = getQuestionScreenProps()
      props.copy(data = props.data.copy(question = None))
    }
    
    //when
    val result = shallowRender(<(QuestionScreen())(^.wrapped := props)())

    //then
    assertNativeComponent(result,
      <.Text()("Loading...")
    )
  }

  it should "render question data " in {
    //given
    val props = getQuestionScreenProps()
    
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
    val Some(question) = props.data.question
    
    assertNativeComponent(result,
      <.ScrollView(
        ^.keyboardShouldPersistTaps := KeyboardShouldPersistTaps.always
      )(),
      { children: List[ShallowInstance] =>
        val List(questionText, choice, text) = children

        assertComponent(questionText, QuestionText) { case QuestionTextProps(html, style) =>
          html shouldBe question.text
          style shouldBe None
        }
        
        assertComponent(choice, choiceGroupComp) {
          case ChoiceGroupProps(items, keyExtractor, _, labelRenderer, selectedIds, _, multiSelect, style) =>
            items shouldBe question.choices
            keyExtractor(question.choices.head) shouldBe 1

            val data = items.head
            assertComponent(wrapAndRender(labelRenderer(data)), QuestionText) {
              case QuestionTextProps(textHtml, choiceStyle) =>
                textHtml shouldBe data.choiceText
                choiceStyle shouldBe Some(styles.choiceLabel)
            }

            selectedIds shouldBe Set.empty
            multiSelect shouldBe false
            style shouldBe Some(styles.choiceGroup)
        }
        
        assertNativeComponent(text,
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
    )
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
