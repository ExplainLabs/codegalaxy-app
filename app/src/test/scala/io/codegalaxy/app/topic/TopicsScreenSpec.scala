package io.codegalaxy.app.topic

import io.codegalaxy.api.topic.{TopicInfoData, TopicWithInfoData}
import io.codegalaxy.app.CodeGalaxyIcons
import io.codegalaxy.app.topic.TopicsScreen._
import io.codegalaxy.app.topic.TopicsScreenSpec.FlatListDataMock
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react._
import scommons.react.test._
import scommons.reactnative.FlatList.FlatListData
import scommons.reactnative._
import scommons.reactnative.svg._

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
    val item = props.data.topics.head

    //when
    val result = flatList.props.keyExtractor(item.asInstanceOf[js.Any])

    //then
    result shouldBe item.data.alias
  }

  it should "render item component" in {
    //given
    val props = getTopicsScreenProps()
    val comp = shallowRender(<(TopicsScreen())(^.wrapped := props)())
    val List(flatList) = findComponents(comp, <.FlatList.reactClass)
    val item = props.data.topics.head
    
    val listData = mock[FlatListDataMock]
    (listData.item _).expects().returning(item)

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
    val data = item.data
    
    assertNativeComponent(result,
      <.TouchableWithoutFeedback()(
        <.View(^.rnStyle := styles.rowContainer)(
          <.View(^.rnStyle := js.Array(styles.iconContainer, styles.icon))(
            item.svgIcon.map { svgXml =>
              <.SvgCss(^.rnStyle := styles.icon, ^.xml := svgXml)()
            }
          ),
          <.View(^.rnStyle := styles.itemContainer)(
            <.Text(^.rnStyle := styles.itemTitle)(data.name),
            data.info.map { info =>
              <.View(^.rnStyle := styles.itemInfoContainer)(
                <(CodeGalaxyIcons.FontAwesome5)(^.rnStyle := styles.itemInfo, ^.name := "language", ^.rnSize := 16)(),
                <.Text(^.rnStyle := styles.itemInfo)(s" : ${data.language}  "),
                <(CodeGalaxyIcons.FontAwesome5)(^.rnStyle := styles.itemInfo, ^.name := "file-code", ^.rnSize := 16)(),
                <.Text(^.rnStyle := styles.itemInfo)(s" : ${info.numberOfQuestions}  "),
                <(CodeGalaxyIcons.FontAwesome5)(^.rnStyle := styles.itemInfo, ^.name := "users", ^.rnSize := 16)(),
                <.Text(^.rnStyle := styles.itemInfo)(s" : ${info.numberOfLearners}")
              )
            }.getOrElse("")
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
          ^.flatListData := js.Array(props.data.topics: _*)
        )()
      )
    )
  }
  
  private def getTopicsScreenProps(dispatch: Dispatch = mock[Dispatch],
                                   actions: TopicActions = mock[TopicActions],
                                   data: TopicState = TopicState(
                                     topics = List(TopicItemState(
                                       data = TopicWithInfoData(
                                         alias = "test",
                                         name = "Test",
                                         language = "en",
                                         info = Some(TopicInfoData(
                                           numberOfQuestions = 1,
                                           numberOfPaid = 2,
                                           numberOfLearners = 3,
                                           numberOfChapters = 4
                                         ))
                                       ),
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
    def item: TopicItemState
  }
}
