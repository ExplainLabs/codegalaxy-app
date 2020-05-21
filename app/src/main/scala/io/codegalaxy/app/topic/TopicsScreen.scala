package io.codegalaxy.app.topic

import io.codegalaxy.app.CodeGalaxyIcons
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react._
import scommons.react.hooks._
import scommons.react.navigation._
import scommons.react.navigation.stack._
import scommons.reactnative.FlatList.FlatListData
import scommons.reactnative._
import scommons.reactnative.svg._

import scala.scalajs.js

case class TopicsScreenProps(dispatch: Dispatch,
                             actions: TopicActions,
                             data: TopicState)

object TopicsScreen extends FunctionComponent[TopicsScreenProps] {

  protected def render(compProps: Props): ReactElement = {
    val props = compProps.wrapped
    
    useEffect({ () =>
      if (props.data.topics.isEmpty) {
        props.dispatch(props.actions.fetchTopics(props.dispatch))
      }
      ()
    }, Nil)

    def renderItem(item: TopicItemState): ReactElement = {
      val data = item.data
      
      <.TouchableWithoutFeedback(^.onPress := { () =>
        //TODO: handle onPress
      })(
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
    }

    <.View(^.rnStyle := styles.container)(
      <.FlatList(
        ^.flatListData := js.Array(props.data.topics: _*),
        ^.renderItem := { data: FlatListData[TopicItemState] =>
          renderItem(data.item)
        },
        ^.keyExtractor := { item: TopicItemState =>
          item.data.alias
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
  }
}
