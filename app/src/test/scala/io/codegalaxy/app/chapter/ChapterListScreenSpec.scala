package io.codegalaxy.app.chapter

import io.codegalaxy.app.CodeGalaxyIcons
import io.codegalaxy.app.chapter.ChapterActions.ChaptersFetchAction
import io.codegalaxy.app.chapter.ChapterListScreen._
import io.codegalaxy.app.info._
import io.codegalaxy.app.topic.TopicParams
import io.codegalaxy.domain.{Chapter, ChapterEntity, ChapterStats}
import org.scalatest.{Assertion, Succeeded}
import scommons.nodejs.test.AsyncTestSpec
import scommons.react._
import scommons.react.navigation._
import scommons.react.redux.Dispatch
import scommons.react.redux.task.FutureTask
import scommons.react.test._
import scommons.reactnative.FlatList.FlatListData
import scommons.reactnative._
import scommons.reactnative.safearea.SafeArea._
import scommons.reactnative.safearea._

import scala.concurrent.Future
import scala.scalajs.js

class ChapterListScreenSpec extends AsyncTestSpec
  with BaseTestSpec
  with TestRendererUtils {

  //noinspection TypeAnnotation
  class Actions {
    val fetchChapters = mockFunction[Dispatch, String, Boolean, ChaptersFetchAction]

    val actions = new MockChapterActions(fetchChaptersMock = fetchChapters)
  }

  it should "call onChapterNavigate when onPress" in {
    //given
    val navigate = mockFunction[String, Unit]
    val props = getChapterListScreenProps(navigate = navigate)
    val item = props.data.chapters.head
    val comp = renderItem(props, item)
    val List(touchable) = findComponents(comp, <.TouchableWithoutFeedback.reactClass)

    //then
    navigate.expects(*).onCall { chapter: String =>
      chapter shouldBe item.entity.alias
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
    val props = getChapterListScreenProps(dispatch, actions.actions)
    val topic = props.params.topic
    val renderer = createTestRenderer(<(ChapterListScreen())(^.wrapped := props)())
    val List(flatList) = findComponents(renderer.root, <.FlatList.reactClass)
    flatList.props.refreshing shouldBe false
    
    val fetchAction = ChaptersFetchAction(topic, FutureTask("Fetching Chapters",
      Future.successful(Nil)))

    //then
    actions.fetchChapters.expects(dispatch, *, *).onCall { (_, t, r) =>
      t shouldBe topic
      r shouldBe true
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
    val topic = "new_topic"
    val props = {
      val props = getChapterListScreenProps(dispatch, actions.actions)
      props.copy(params = props.params.copy(topic = topic))
    }
    val fetchAction = ChaptersFetchAction(topic, FutureTask("Fetching Chapters",
      Future.successful(Nil)))
    
    //then
    actions.fetchChapters.expects(dispatch, *, *).onCall { (_, t, r) =>
      t shouldBe topic
      r shouldBe false
      fetchAction
    }
    dispatch.expects(fetchAction)

    //when
    testRender(<(ChapterListScreen())(^.wrapped := props)())

    //then
    fetchAction.task.future.map { _ =>
      Succeeded
    }
  }

  it should "not dispatch actions if params not changed when mount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = new Actions
    val props = getChapterListScreenProps(dispatch, actions.actions)
    
    //then
    actions.fetchChapters.expects(*, *, *).never()

    //when
    testRender(<(ChapterListScreen())(^.wrapped := props)())

    Succeeded
  }

  it should "return data.alias from keyExtractor" in {
    //given
    val props = getChapterListScreenProps()
    val comp = testRender(<(ChapterListScreen())(^.wrapped := props)())
    val List(flatList) = findComponents(comp, <.FlatList.reactClass)
    val item = props.data.chapters.head

    //when
    val result = flatList.props.keyExtractor(item.asInstanceOf[js.Any])

    //then
    result shouldBe item.entity.alias
  }

  it should "render item component with Start action" in {
    //given
    val props = getChapterListScreenProps()
    val item = props.data.chapters.head
    item.stats.map(_.progress).getOrElse(0) shouldBe 0
    
    //when
    val result = renderItem(props, item)

    //then
    assertItem(result, item)
  }

  it should "render item component with Open action" in {
    //given
    val props = {
      val props = getChapterListScreenProps()
      props.copy(data = props.data.copy(
        chapters = props.data.chapters.map(c => c.copy(stats = Some(c.stats.get.copy(progress = 99))))
      ))
    }
    val item = props.data.chapters.head
    item.stats.map(_.progress).getOrElse(0) should be > 0
    
    //when
    val result = renderItem(props, item)

    //then
    assertItem(result, item)
  }

  it should "render main component" in {
    //given
    val props = getChapterListScreenProps()
    val component = <(ChapterListScreen())(^.wrapped := props)()

    //when
    val result = testRender(component)

    //then
    assertNativeComponent(result,
      <.SafeAreaView(
        ^.rnStyle := styles.container,
        ^.edges := List(SafeAreaEdge.left, SafeAreaEdge.bottom, SafeAreaEdge.right)
      )(
        <.FlatList(
          ^.refreshing := false,
          ^.flatListData := js.Array(props.data.chapters: _*)
        )()
      )
    )
  }

  private def getChapterListScreenProps(dispatch: Dispatch = mock[Dispatch],
                                        actions: ChapterActions = new Actions().actions,
                                        data: ChapterState = ChapterState(
                                          topic = Some("test_topic"),
                                          chapters = List(Chapter(
                                            entity = ChapterEntity(
                                              id = 1,
                                              topic = "test_topic",
                                              alias = "test_chapter",
                                              name = "Test Chapter",
                                              numQuestions = 1,
                                              numPaid = 2,
                                              numLearners = 3,
                                              numChapters = 4,
                                              numTheory = Some(5)
                                            ),
                                            stats = Some(ChapterStats(
                                              id = 1,
                                              progress = 0,
                                              progressOnce = 0,
                                              progressAll = 0,
                                              freePercent = 0,
                                              paid = 0
                                            ))
                                          ))
                                        ),
                                        params: TopicParams = TopicParams("test_topic"),
                                        navigate: String => Unit = _ => ()): ChapterListScreenProps = {
    ChapterListScreenProps(
      dispatch = dispatch,
      actions = actions,
      data = data,
      params = params,
      navigate = navigate
    )
  }

  private def renderItem(props: ChapterListScreenProps, data: Chapter): TestInstance = {
    val comp = testRender(<(ChapterListScreen())(^.wrapped := props)())
    val List(flatList) = findComponents(comp, <.FlatList.reactClass)

    val listData = js.Dynamic.literal("item" -> data.asInstanceOf[js.Any])
    val result = flatList.props.renderItem(listData.asInstanceOf[FlatListData[Chapter]])
    createTestRenderer(result.asInstanceOf[ReactElement]).root
  }

  private def assertItem(result: TestInstance, data: Chapter): Assertion = {
    implicit val theme: Theme = DefaultTheme
    
    assertNativeComponent(result, <.TouchableWithoutFeedback()(), inside(_) { case List(container) =>
      assertNativeComponent(container, <.View(^.rnStyle := styles.rowContainer)(), inside(_) { case List(info, action) =>
        assertNativeComponent(info,
          <.View(^.rnStyle := styles.itemContainer)(
            <.Text(themeStyle(styles.itemTitle, themeTextStyle))(data.entity.name),
            <.View(^.rnStyle := styles.itemInfoContainer)(
              <(CodeGalaxyIcons.FontAwesome5)(themeStyle(styles.itemInfo, styles.itemInfoDark), ^.name := "file-code", ^.rnSize := 16)(),
              <.Text(themeStyle(styles.itemInfo, styles.itemInfoDark))(s" : ${data.entity.numQuestions}")
            )
          )
        )
        assertTestComponent(action, ListItemNavIcon) { case ListItemNavIconProps(progress, showLabel) =>
          progress shouldBe data.stats.map(_.progress).getOrElse(0)
          showLabel shouldBe false
        }
      })
    })
  }
}
