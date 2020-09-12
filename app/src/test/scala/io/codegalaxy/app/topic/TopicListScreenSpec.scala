package io.codegalaxy.app.topic

import io.codegalaxy.app.CodeGalaxyIcons
import io.codegalaxy.app.info._
import io.codegalaxy.app.topic.TopicActions.TopicsFetchAction
import io.codegalaxy.app.topic.TopicListScreen._
import io.codegalaxy.app.topic.TopicListScreenSpec.FlatListDataMock
import io.codegalaxy.domain.TopicEntity
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import org.scalatest.{Assertion, Succeeded}
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

class TopicListScreenSpec extends AsyncTestSpec
  with BaseTestSpec
  with ShallowRendererUtils
  with TestRendererUtils {

  it should "call onTopicNavigate when onPress" in {
    //given
    val navigate = mockFunction[String, Unit]
    val props = getTopicListScreenProps(navigate = navigate)
    val item = props.data.topics.head
    val comp = renderItem(props, item)
    val List(touchable) = findComponents(comp, <.TouchableWithoutFeedback.reactClass)

    //then
    navigate.expects(item.alias)

    //when
    touchable.props.onPress()
    
    Succeeded
  }

  it should "dispatch actions when onRefresh" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[TopicActions]
    val props = getTopicListScreenProps(dispatch, actions)
    val renderer = createTestRenderer(<(TopicListScreen())(^.wrapped := props)())
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
      val props = getTopicListScreenProps(dispatch, actions)
      props.copy(data = props.data.copy(topics = Nil))
    }
    val fetchAction = TopicsFetchAction(FutureTask("Fetching Topics",
      Future.successful(Nil)))
    
    //then
    (actions.fetchTopics _).expects(dispatch, false).returning(fetchAction)
    dispatch.expects(fetchAction)

    //when
    val result = testRender(<(TopicListScreen())(^.wrapped := props)())

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

  it should "not dispatch actions if topics list is not empty when mount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[TopicActions]
    val props = getTopicListScreenProps(dispatch, actions)
    
    //then
    (actions.fetchTopics _).expects(*, *).never()

    //when
    testRender(<(TopicListScreen())(^.wrapped := props)())

    Succeeded
  }

  it should "return data.alias from keyExtractor" in {
    //given
    val props = getTopicListScreenProps()
    val comp = shallowRender(<(TopicListScreen())(^.wrapped := props)())
    val List(flatList) = findComponents(comp, <.FlatList.reactClass)
    val item = props.data.topics.head

    //when
    val result = flatList.props.keyExtractor(item.asInstanceOf[js.Any])

    //then
    result shouldBe item.alias
  }

  it should "render item component with Start action" in {
    //given
    val props = getTopicListScreenProps()
    val item = props.data.topics.head
    item.progress shouldBe None
    
    //when
    val result = renderItem(props, item)

    //then
    assertItem(result, item)
  }

  it should "render item component with Open action" in {
    //given
    val props = {
      val props = getTopicListScreenProps()
      props.copy(data = props.data.copy(
        topics = props.data.topics.map(_.copy(progress = Some(99)))
      ))
    }
    val item = props.data.topics.head
    item.progress should not be None
    
    //when
    val result = renderItem(props, item)

    //then
    assertItem(result, item)
  }

  it should "render main component" in {
    //given
    val props = getTopicListScreenProps()
    val component = <(TopicListScreen())(^.wrapped := props)()

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

  private def getTopicListScreenProps(dispatch: Dispatch = mock[Dispatch],
                                      actions: TopicActions = mock[TopicActions],
                                      data: TopicState = TopicState(
                                        topics = List(TopicEntity(
                                          id = 1,
                                          alias = "test_topic",
                                          name = "Test Topic",
                                          lang = "en",
                                          numQuestions = 1,
                                          numPaid = 2,
                                          numLearners = 3,
                                          numChapters = 4,
                                          svgIcon = Some("svg-xml"),
                                          progress = None
                                        ))
                                      ),
                                      navigate: String => Unit = _ => ()): TopicListScreenProps = {
    TopicListScreenProps(
      dispatch = dispatch,
      actions = actions,
      data = data,
      navigate = navigate
    )
  }

  private def renderItem(props: TopicListScreenProps, data: TopicEntity): ShallowInstance = {
    val comp = shallowRender(<(TopicListScreen())(^.wrapped := props)())
    val List(flatList) = findComponents(comp, <.FlatList.reactClass)

    val listData = mock[FlatListDataMock]
    (listData.item _).expects().returning(data)

    val wrapper = new FunctionComponent[Unit] {
      protected def render(compProps: Props): ReactElement = {
        val result = flatList.props.renderItem(listData.asInstanceOf[FlatListData[TopicEntity]])
        result.asInstanceOf[ReactElement]
      }
    }

    shallowRender(<(wrapper())()())
  }

  private def assertItem(result: ShallowInstance, data: TopicEntity): Assertion = {
    assertNativeComponent(result, <.TouchableWithoutFeedback()(), { children: List[ShallowInstance] =>
      val List(container) = children
      
      assertNativeComponent(container, <.View(^.rnStyle := styles.rowContainer)(), { children: List[ShallowInstance] =>
        val List(icon, info, action) = children
        
        assertNativeComponent(icon, 
          <.View(^.rnStyle := js.Array(styles.iconContainer, styles.icon))(
            data.svgIcon.map { svgXml =>
              <.SvgCss(^.rnStyle := styles.icon, ^.xml := svgXml)()
            }
          )
        )
        assertNativeComponent(info,
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
        assertComponent(action, ListItemNavIcon) { case ListItemNavIconProps(progress, showLabel) =>
          progress shouldBe data.progress.getOrElse(0)
          showLabel shouldBe true
        }
      })
    })
  }
}

object TopicListScreenSpec {

  @JSExportAll
  trait FlatListDataMock {
    def item: TopicEntity
  }
}
