package io.codegalaxy.app.chapter

import io.codegalaxy.app.CodeGalaxyIcons
import io.codegalaxy.app.info._
import io.codegalaxy.app.topic.TopicParams
import io.codegalaxy.domain.ChapterEntity
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react._
import scommons.react.hooks._
import scommons.reactnative.FlatList.FlatListData
import scommons.reactnative._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js

case class ChapterListScreenProps(dispatch: Dispatch,
                                  actions: ChapterActions,
                                  data: ChapterState,
                                  params: TopicParams,
                                  navigate: String => Unit)

object ChapterListScreen extends FunctionComponent[ChapterListScreenProps] {

  protected def render(compProps: Props): ReactElement = {
    val (refreshing, setRefreshing) = useState(false)
    val props = compProps.wrapped
    val topic = props.params.topic
    
    useEffect({ () =>
      if (!props.data.topic.contains(topic)) {
        props.dispatch(props.actions.fetchChapters(props.dispatch, topic))
      }
      ()
    }, Nil)

    def renderItem(data: ChapterEntity): ReactElement = {
      <.TouchableWithoutFeedback(^.onPress := { () =>
        props.navigate(data.alias)
      })(
        <.View(^.rnStyle := styles.rowContainer)(
          <.View(^.rnStyle := styles.itemContainer)(
            <.Text(^.rnStyle := styles.itemTitle)(data.name),
            <.View(^.rnStyle := styles.itemInfoContainer)(
              <(CodeGalaxyIcons.FontAwesome5)(^.rnStyle := styles.itemInfo, ^.name := "file-code", ^.rnSize := 16)(),
              <.Text(^.rnStyle := styles.itemInfo)(s" : ${data.numQuestions}")
            )
          ),
          <(ListItemNavIcon())(^.wrapped := ListItemNavIconProps(
            progress = data.progress,
            showLabel = false
          ))()
        )
      )
    }

    <.View(^.rnStyle := styles.container)(
      <.FlatList(
        ^.refreshing := refreshing,
        ^.onRefresh := { () =>
          if (!refreshing) {
            setRefreshing(true)
            val fetchAction = props.actions.fetchChapters(props.dispatch, topic, refresh = true)
            props.dispatch(fetchAction)

            fetchAction.task.future.andThen { case _ =>
              setRefreshing(false)
            }
          }
        },
        ^.flatListData := js.Array(props.data.chapters: _*),
        ^.renderItem := { data: FlatListData[ChapterEntity] =>
          renderItem(data.item)
        },
        ^.keyExtractor := { item: ChapterEntity =>
          item.alias
        }
      )()
    )
  }

  private[chapter] lazy val styles = StyleSheet.create(new Styles)
  private[chapter] class Styles extends js.Object {
    import Style._
    import TextStyle._
    import ViewStyle._

    val container: Style = new ViewStyle {
      override val flex = 1
    }
    val rowContainer: Style = new ViewStyle {
      override val flexDirection = FlexDirection.row
      override val alignItems = AlignItems.center
      override val paddingLeft = 10
      override val paddingRight = 10
      override val borderBottomWidth = 2
      override val borderBottomColor = Color.darkgray
    }
    val itemContainer: Style = new ViewStyle {
      override val padding = 10
    }
    val itemTitle: Style = new TextStyle {
      override val fontWeight = FontWeight.bold
      override val marginBottom = 5
    }
    val itemInfoContainer: Style = new ViewStyle {
      override val flexDirection = FlexDirection.row
      override val alignItems = AlignItems.center
    }
    val itemInfo: Style = new TextStyle {
      override val color = "rgba(0, 0, 0, .5)"
    }
  }
}
