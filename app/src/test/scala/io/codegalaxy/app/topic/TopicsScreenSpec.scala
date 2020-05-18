package io.codegalaxy.app.topic

import io.codegalaxy.api.topic.{TopicInfoData, TopicWithInfoData}
import io.codegalaxy.app.topic.TopicsScreen._
import io.codegalaxy.app.topic.TopicsScreenSpec.FlatListDataMock
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react._
import scommons.react.test._
import scommons.reactnative.FlatList.FlatListData
import scommons.reactnative._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportAll

class TopicsScreenSpec extends TestSpec with ShallowRendererUtils {

  ignore should "call navigate when onPress" in {
    //given
    val navigate = mockFunction[String, Unit]
    val props = getTopicsScreenProps(navigate = navigate)
    val comp = shallowRender(<(TopicsScreen())(^.wrapped := props)())
    val List(touchable) = findComponents(comp, <.TouchableWithoutFeedback.reactClass)

    //then
    navigate.expects("Test")

    //when
    touchable.props.onPress()
  }

  it should "return data.alias from keyExtractor" in {
    //given
    val props = getTopicsScreenProps()
    val comp = shallowRender(<(TopicsScreen())(^.wrapped := props)())
    val List(flatList) = findComponents(comp, <.FlatList.reactClass)
    val data = props.data.topics.head

    //when
    val result = flatList.props.keyExtractor(data.asInstanceOf[js.Any])

    //then
    result shouldBe data.alias
  }

  it should "render item component" in {
    //given
    val props = getTopicsScreenProps()
    val comp = shallowRender(<(TopicsScreen())(^.wrapped := props)())
    val List(flatList) = findComponents(comp, <.FlatList.reactClass)
    val data = props.data.topics.head
    
    val listData = mock[FlatListDataMock]
    (listData.item _).expects().returning(data)

    def renderItem(flatList: ShallowInstance): ShallowInstance = {
      val wrapper = new FunctionComponent[Unit] {
        protected def render(compProps: Props): ReactElement = {
          val result = flatList.props.renderItem(listData.asInstanceOf[FlatListData[TopicWithInfoData]])
          result.asInstanceOf[ReactElement]
        }
      }

      shallowRender(<(wrapper())()())
    }

    //when
    val result = renderItem(flatList)

    //then
    assertNativeComponent(result,
      <.TouchableWithoutFeedback()(
        <.View(^.rnStyle := styles.itemContainer)(
          <.Text(^.rnStyle := styles.itemTitle)(data.name),
          <.Text(^.rnStyle := styles.itemDescription)(data.info.map { info =>
            s"Lang: ${data.language}" +
              s", Questions: ${info.numberOfQuestions}" +
              s", Learners: ${info.numberOfLearners}"
          }.getOrElse(""))
        )
      )
    )
  }

  it should "render main component" in {
    //given
    val props = getTopicsScreenProps()
    val component = <(TopicsScreen())(^.wrapped := props)()

    //when
    val result = shallowRender(component)

    //then
    assertNativeComponent(result,
      <.View(^.rnStyle := styles.container)(
        <.FlatList(
          ^.flatListData := js.Array(props.data.topics: _*)
        )()
      )
    )
  }
  
  private def getTopicsScreenProps(dispatch: Dispatch = mock[Dispatch],
                                   actions: TopicActions = mock[TopicActions],
                                   data: TopicState = TopicState(
                                     topics = List(TopicWithInfoData(
                                       alias = "test",
                                       name = "Test",
                                       language = "en",
                                       audience = Some("Test audience"),
                                       info = Some(TopicInfoData(
                                         numberOfQuestions = 1,
                                         numberOfPaid = 2,
                                         numberOfLearners = 3,
                                         numberOfChapters = 4
                                       ))
                                     ))
                                   ),
                                   navigate: String => Unit = _ => ()): TopicsScreenProps = {
    TopicsScreenProps(
      dispatch = dispatch,
      actions = actions,
      data = data
    )
  }
}

object TopicsScreenSpec {

  @JSExportAll
  trait FlatListDataMock {
    def item: TopicWithInfoData
  }
}
