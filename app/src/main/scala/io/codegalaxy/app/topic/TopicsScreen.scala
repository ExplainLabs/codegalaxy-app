package io.codegalaxy.app.topic

import io.codegalaxy.app.CodeGalaxyIcons
import io.codegalaxy.domain.TopicEntity
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react._
import scommons.react.hooks._
import scommons.react.navigation._
import scommons.react.navigation.stack._
import scommons.reactnative.FlatList.FlatListData
import scommons.reactnative._
import scommons.reactnative.svg._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js

case class TopicsScreenProps(dispatch: Dispatch,
                             actions: TopicActions,
                             data: TopicState)

object TopicsScreen extends FunctionComponent[TopicsScreenProps] {

  protected def render(compProps: Props): ReactElement = {
    val (refreshing, setRefreshing) = useState(false)
    val props = compProps.wrapped
    
    useEffect({ () =>
      if (props.data.topics.isEmpty) {
        props.dispatch(props.actions.fetchTopics(props.dispatch))
      }
      ()
    }, Nil)

    def renderItem(data: TopicEntity): ReactElement = {
      <.TouchableWithoutFeedback(^.onPress := { () =>
        //TODO: handle onPress
      })(
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
          ),
          <.View(^.rnStyle := styles.statsContainer)(data.progress match {
            case Some(progress) => <.>()(
              <.Text(^.rnStyle := styles.statsLabel)("Open"),
              <.View(^.rnStyle := styles.statsProgress)(
                <.Text()(s"$progress")
              )
            )
            case None => <.>()(
              <.Text(^.rnStyle := styles.statsLabel)("Start"),
              <.SvgXml(^.rnStyle := styles.startSvg, ^.xml := startSvgXml)()
            )
          })
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
        ^.renderItem := { data: FlatListData[TopicEntity] =>
          renderItem(data.item)
        },
        ^.keyExtractor := { item: TopicEntity =>
          item.alias
        }
      )()
    )
  }

  private[topic] lazy val Stack = createStackNavigator()

  def topicStackComp(topicsController: TopicsController): ReactClass = new FunctionComponent[Unit] {
    protected def render(props: Props): ReactElement = {
      <(Stack.Navigator)(^.initialRouteName := "Quizzes")(
        <(Stack.Screen)(^.name := "Quizzes", ^.component := topicsController())()
      )
    }
  }.apply()

  private[topic] lazy val startSvgXml =
    """<svg width="32" height="32" viewBox="0 0 512 512">
      |  <path
      |    fill="#1e90ff"
      |    d="M256 504c137 0 248-111 248-248S393 8 256 8 8 119 8 256s111 248 248 248zM40 256c0-118.7 96.1-216 216-216 118.7 0 216 96.1 216 216 0 118.7-96.1 216-216 216-118.7 0-216-96.1-216-216zm331.7-18l-176-107c-15.8-8.8-35.7 2.5-35.7 21v208c0 18.4 19.8 29.8 35.7 21l176-101c16.4-9.1 16.4-32.8 0-42zM192 335.8V176.9c0-4.7 5.1-7.6 9.1-5.1l134.5 81.7c3.9 2.4 3.8 8.1-.1 10.3L201 341c-4 2.3-9-.6-9-5.2z"
      |  />
      |</svg>
      |""".stripMargin

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
    val statsContainer: Style = new ViewStyle {
      override val flex = 1
      override val flexDirection = FlexDirection.row
      override val flexWrap = FlexWrap.wrap
      override val alignItems = AlignItems.center
      override val justifyContent = JustifyContent.`flex-end`
    }
    val statsLabel: Style = new TextStyle {
      override val color = Color.dodgerblue
      override val fontWeight = FontWeight.bold
      override val marginRight = 5
    }
    val statsProgress: Style = new ViewStyle {
      override val alignItems = AlignItems.center
      override val justifyContent = JustifyContent.center
      override val width = 32
      override val height = 32
      override val borderRadius = 16
      override val borderWidth = 2
      override val borderColor = Color.dodgerblue
    }
    val startSvg: Style = new ViewStyle {
      override val color = Color.dodgerblue
      override val width = "100%"
      override val height = "100%"
    }
  }
}
