package io.codegalaxy.app.topic

import io.codegalaxy.api.topic.TopicWithInfoData
import io.codegalaxy.app.CodeGalaxyIcons
import io.codegalaxy.app.topic.TopicActions.TopicsFetchAction
import io.codegalaxy.app.topic.TopicsScreen._
import io.codegalaxy.app.topic.TopicsScreenSpec.FlatListDataMock
import io.codegalaxy.domain.TopicEntity
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import org.scalatest.Succeeded
import scommons.nodejs.test.AsyncTestSpec
import scommons.react._
import scommons.react.redux.task.FutureTask
import scommons.react.test._
import scommons.reactnative.FlatList.FlatListData
import scommons.reactnative._
import scommons.reactnative.svg._

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportAll

class TopicsScreenSpec extends AsyncTestSpec
  with BaseTestSpec
  with ShallowRendererUtils
  with TestRendererUtils {

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
    
    Succeeded
  }

  it should "dispatch actions when onRefresh" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[TopicActions]
    val props = getTopicsScreenProps(dispatch, actions)
    val renderer = createTestRenderer(<(TopicsScreen())(^.wrapped := props)())
    val List(flatList) = findComponents(renderer.root, <.FlatList.reactClass)
    flatList.props.refreshing shouldBe false
    
    val fetchAction = TopicsFetchAction(FutureTask("Fetching Topics",
      Future.successful(Nil)))
    
    //then
    (actions.fetchTopics _).expects(dispatch, true).returning(fetchAction)
    dispatch.expects(fetchAction)

    //when
    flatList.props.onRefresh()

    //then
    flatList.props.refreshing shouldBe true
    
    eventually {
      flatList.props.refreshing shouldBe false
    }
  }

  it should "dispatch actions when mount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[TopicActions]
    val props = {
      val props = getTopicsScreenProps(dispatch, actions)
      props.copy(data = props.data.copy(topics = Nil))
    }
    val fetchAction = TopicsFetchAction(FutureTask("Fetching Topics",
      Future.successful(Nil)))
    
    //then
    (actions.fetchTopics _).expects(dispatch, false).returning(fetchAction)
    dispatch.expects(fetchAction)

    //when
    val result = testRender(<(TopicsScreen())(^.wrapped := props)())

    //then
    assertNativeComponent(result,
      <.View(^.rnStyle := styles.container)(
        <.FlatList(
          ^.refreshing := false,
          ^.flatListData := js.Array(props.data.topics: _*)
        )()
      )
    )
  }

  it should "return data.alias from keyExtractor" in {
    //given
    val props = getTopicsScreenProps()
    val comp = shallowRender(<(TopicsScreen())(^.wrapped := props)())
    val List(flatList) = findComponents(comp, <.FlatList.reactClass)
    val item = props.data.topics.head

    //when
    val result = flatList.props.keyExtractor(item.asInstanceOf[js.Any])

    //then
    result shouldBe item.alias
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
        <.View(^.rnStyle := styles.rowContainer)(
          <.View(^.rnStyle := js.Array(styles.iconContainer, styles.icon))(
            data.svgIcon.map { svgXml =>
              <.SvgCss(^.rnStyle := styles.icon, ^.xml := svgXml)()
            }
          ),
          <.View(^.rnStyle := styles.itemContainer)(
            <.Text(^.rnStyle := styles.itemTitle)(data.name),
            <.View(^.rnStyle := styles.itemInfoContainer)(
              <(CodeGalaxyIcons.FontAwesome5)(^.rnStyle := styles.itemInfo, ^.name := "language", ^.rnSize := 16)(),
              <.Text(^.rnStyle := styles.itemInfo)(s" : ${data.lang}  "),
              <(CodeGalaxyIcons.FontAwesome5)(^.rnStyle := styles.itemInfo, ^.name := "file-code", ^.rnSize := 16)(),
              <.Text(^.rnStyle := styles.itemInfo)(s" : ${data.numQuestions}  "),
              <(CodeGalaxyIcons.FontAwesome5)(^.rnStyle := styles.itemInfo, ^.name := "users", ^.rnSize := 16)(),
              <.Text(^.rnStyle := styles.itemInfo)(s" : ${data.numLearners}")
            )
          )
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
          ^.refreshing := false,
          ^.flatListData := js.Array(props.data.topics: _*)
        )()
      )
    )
  }
  
  private def getTopicsScreenProps(dispatch: Dispatch = mock[Dispatch],
                                   actions: TopicActions = mock[TopicActions],
                                   data: TopicState = TopicState(
                                     topics = List(TopicEntity(
                                       id = 1,
                                       alias = "test",
                                       name = "Test",
                                       lang = "en",
                                       numQuestions = 1,
                                       numPaid = 2,
                                       numLearners = 3,
                                       numChapters = 4,
                                       svgIcon = Some("svg-xml")
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
    def item: TopicEntity
  }
}
