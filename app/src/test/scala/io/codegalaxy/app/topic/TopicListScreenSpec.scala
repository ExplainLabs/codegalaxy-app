package io.codegalaxy.app.topic

import io.codegalaxy.app.CodeGalaxyIcons
import io.codegalaxy.app.info._
import io.codegalaxy.app.topic.TopicActions.TopicsFetchAction
import io.codegalaxy.app.topic.TopicListScreen._
import io.codegalaxy.domain.{Topic, TopicEntity, TopicStats}
import org.scalatest.{Assertion, Succeeded}
import scommons.nodejs.test.AsyncTestSpec
import scommons.react._
import scommons.react.navigation._
import scommons.react.redux.Dispatch
import scommons.react.redux.task.FutureTask
import scommons.react.test._
import scommons.reactnative.FlatList.FlatListData
import scommons.reactnative._
import scommons.reactnative.svg._

import scala.concurrent.Future
import scala.scalajs.js

class TopicListScreenSpec extends AsyncTestSpec
  with BaseTestSpec
  with TestRendererUtils {

  //noinspection TypeAnnotation
  class Actions {
    val fetchTopics = mockFunction[Dispatch, Boolean, TopicsFetchAction]

    val actions = new MockTopicActions(fetchTopicsMock = fetchTopics)
  }

  it should "call onTopicNavigate when onPress" in {
    //given
    val navigate = mockFunction[String, Unit]
    val props = getTopicListScreenProps(navigate = navigate)
    val item = props.data.topics.head
    val comp = renderItem(props, item)
    val touchable = inside(findComponents(comp, <.TouchableWithoutFeedback.reactClass)) {
      case List(touchable) => touchable
    }

    //then
    navigate.expects(*).onCall { topic: String =>
      topic shouldBe item.entity.alias
      ()
    }

    //when
    touchable.props.onPress()
    
    Succeeded
  }

  it should "dispatch actions when onRefresh" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = new Actions
    val props = getTopicListScreenProps(dispatch, actions.actions)
    val renderer = createTestRenderer(<(TopicListScreen())(^.wrapped := props)())
    val flatList = inside(findComponents(renderer.root, <.FlatList.reactClass)) {
      case List(flatList) => flatList
    }
    flatList.props.refreshing shouldBe false
    
    val fetchAction = TopicsFetchAction(FutureTask("Fetching Topics",
      Future.successful(Nil)))
    
    //then
    actions.fetchTopics.expects(dispatch, *).onCall { (_, refresh) =>
      refresh shouldBe true
      fetchAction
    }
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
    val actions = new Actions
    val props = {
      val props = getTopicListScreenProps(dispatch, actions.actions)
      props.copy(data = props.data.copy(topics = Nil))
    }
    val fetchAction = TopicsFetchAction(FutureTask("Fetching Topics",
      Future.successful(Nil)))
    
    //then
    actions.fetchTopics.expects(dispatch, *).onCall { (_, refresh) =>
      refresh shouldBe false
      fetchAction
    }
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
    val actions = new Actions
    val props = getTopicListScreenProps(dispatch, actions.actions)
    
    //then
    actions.fetchTopics.expects(*, *).never()

    //when
    testRender(<(TopicListScreen())(^.wrapped := props)())

    Succeeded
  }

  it should "return data.alias from keyExtractor" in {
    //given
    val props = getTopicListScreenProps()
    val comp = testRender(<(TopicListScreen())(^.wrapped := props)())
    val flatList = inside(findComponents(comp, <.FlatList.reactClass)) {
      case List(flatList) => flatList
    }
    val item = props.data.topics.head

    //when
    val result = flatList.props.keyExtractor(item.asInstanceOf[js.Any])

    //then
    result shouldBe item.entity.alias
  }

  it should "render item component with Start action" in {
    //given
    val props = getTopicListScreenProps()
    val item = props.data.topics.head
    item.stats shouldBe None
    
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
        topics = props.data.topics.map(c => c.copy(stats = Some(TopicStats(
          id = c.entity.id,
          progress = 10,
          progressOnce = 20,
          progressAll = 88,
          freePercent = 30,
          paid = 40
        ))))
      ))
    }
    val item = props.data.topics.head
    item.stats should not be None
    
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
    val result = testRender(component)

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
                                      actions: TopicActions = new Actions().actions,
                                      data: TopicState = TopicState(
                                        topics = List(Topic(
                                          entity = TopicEntity(
                                            id = 1,
                                            alias = "test_topic",
                                            name = "Test Topic",
                                            lang = "en",
                                            numQuestions = 1,
                                            numPaid = 2,
                                            numLearners = 3,
                                            numChapters = 4,
                                            numTheory = Some(5),
                                            svgIcon = Some("svg-xml")
                                          ),
                                          stats = None
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

  private def renderItem(props: TopicListScreenProps, data: Topic): TestInstance = {
    val comp = testRender(<(TopicListScreen())(^.wrapped := props)())
    val flatList = inside(findComponents(comp, <.FlatList.reactClass)) {
      case List(flatList) => flatList
    }

    val listData = js.Dynamic.literal("item" -> data.asInstanceOf[js.Any])
    val result = flatList.props.renderItem(listData.asInstanceOf[FlatListData[Topic]])
    createTestRenderer(result.asInstanceOf[ReactElement]).root
  }

  private def assertItem(result: TestInstance, data: Topic): Assertion = {
    implicit val theme: Theme = DefaultTheme
    
    assertNativeComponent(result, <.TouchableWithoutFeedback()(), inside(_) { case List(container) =>
      assertNativeComponent(container, <.View(^.rnStyle := styles.rowContainer)(), inside(_) { case List(icon, info, action) =>
        assertNativeComponent(icon, 
          <.View(^.rnStyle := js.Array(styles.iconContainer, styles.icon))(
            data.entity.svgIcon.map { svgXml =>
              <.SvgCss(^.rnStyle := styles.icon, ^.xml := svgXml)()
            }
          )
        )
        assertNativeComponent(info,
          <.View(^.rnStyle := styles.itemContainer)(
            <.Text(themeStyle(styles.itemTitle, themeTextStyle))(data.entity.name),
            <.View(^.rnStyle := styles.itemInfoContainer)(
              <(CodeGalaxyIcons.FontAwesome5)(themeStyle(styles.itemInfo, styles.itemInfoDark), ^.name := "language", ^.rnSize := 16)(),
              <.Text(themeStyle(styles.itemInfo, styles.itemInfoDark))(s" : ${data.entity.lang}  "),
              <(CodeGalaxyIcons.FontAwesome5)(themeStyle(styles.itemInfo, styles.itemInfoDark), ^.name := "file-code", ^.rnSize := 16)(),
              <.Text(themeStyle(styles.itemInfo, styles.itemInfoDark))(s" : ${data.entity.numQuestions}  "),
              <(CodeGalaxyIcons.FontAwesome5)(themeStyle(styles.itemInfo, styles.itemInfoDark), ^.name := "users", ^.rnSize := 16)(),
              <.Text(themeStyle(styles.itemInfo, styles.itemInfoDark))(s" : ${data.entity.numLearners}")
            )
          )
        )
        assertTestComponent(action, ListItemNavIcon) { case ListItemNavIconProps(progress, showLabel) =>
          progress shouldBe data.stats.map(_.progress).getOrElse(0)
          showLabel shouldBe true
        }
      })
    })
  }
}
