package io.codegalaxy.app.topic

import io.codegalaxy.app.CodeGalaxyIcons
import io.codegalaxy.app.info._
import io.codegalaxy.domain.Topic
import scommons.react._
import scommons.react.hooks._
import scommons.react.navigation._
import scommons.react.redux.Dispatch
import scommons.reactnative.FlatList.FlatListData
import scommons.reactnative._
import scommons.reactnative.svg._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js

case class TopicListScreenProps(dispatch: Dispatch,
                                actions: TopicActions,
                                data: TopicState,
                                navigate: String => Unit)

object TopicListScreen extends FunctionComponent[TopicListScreenProps] {

  protected def render(compProps: Props): ReactElement = {
    implicit val theme: Theme = useTheme()
    val (refreshing, setRefreshing) = useState(false)
    val props = compProps.wrapped
    
    useEffect({ () =>
      if (props.data.topics.isEmpty) {
        props.dispatch(props.actions.fetchTopics(props.dispatch))
      }
      ()
    }, Nil)

    def renderItem(data: Topic): ReactElement = {
      <.TouchableWithoutFeedback(^.onPress := { () =>
        props.navigate(data.entity.alias)
      })(
        <.View(^.rnStyle := styles.rowContainer)(
          <.View(^.rnStyle := js.Array(styles.iconContainer, styles.icon))(
            data.entity.svgIcon.map { svgXml =>
              <.SvgCss(^.rnStyle := styles.icon, ^.xml := svgXml)()
            }
          ),
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
          ),
          <(ListItemNavIcon())(^.wrapped := ListItemNavIconProps(
            progress = data.stats.map(_.progress).getOrElse(0),
            showLabel = true
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
            val fetchAction = props.actions.fetchTopics(props.dispatch, refresh = true)
            props.dispatch(fetchAction)

            fetchAction.task.future.andThen { case _ =>
              setRefreshing(false)
            }
          }
        },
        ^.flatListData := js.Array(props.data.topics: _*),
        ^.renderItem := { data: FlatListData[Topic] =>
          renderItem(data.item)
        },
        ^.keyExtractor := { item: Topic =>
          item.entity.alias
        }
      )()
    )
  }

  private[topic] lazy val styles = StyleSheet.create(new Styles)
  private[topic] class Styles extends js.Object {
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
    val iconContainer: Style = new ViewStyle {
      override val alignItems = AlignItems.center
      override val backgroundColor = Color.black
    }
    val icon: Style = new ViewStyle {
      override val width = 50
      override val height = 50
      override val borderRadius = 25
    }
    val itemContainer: Style = new ViewStyle {
      override val padding = 10
    }
    val itemTitle: Style = new TextStyle {
      override val fontSize = 20
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
    val itemInfoDark: Style = new TextStyle {
      override val color = "rgba(255, 255, 255, .5)"
    }
  }
}
