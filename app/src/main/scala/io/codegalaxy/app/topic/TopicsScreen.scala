package io.codegalaxy.app.topic

import io.codegalaxy.api.topic.TopicWithInfoData
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react._
import scommons.react.hooks._
import scommons.react.navigation._
import scommons.react.navigation.stack._
import scommons.reactnative.FlatList.FlatListData
import scommons.reactnative._

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

    def renderItem(data: TopicWithInfoData): ReactElement = {
      <.TouchableWithoutFeedback(^.onPress := { () =>
        //TODO: handle onPress
      })(
        <.View(^.rnStyle := styles.itemContainer)(
          <.Text(^.rnStyle := styles.itemTitle)(data.name),
          <.Text(^.rnStyle := styles.itemDescription)(data.info.map { info =>
            s"Lang: ${data.language}" +
              s", Questions: ${info.numberOfQuestions}" +
              s", Learners: ${info.numberOfLearners}"
          }.getOrElse(""))
        )
      )
    }

    <.View(^.rnStyle := styles.container)(
      <.FlatList(
        ^.flatListData := js.Array(props.data.topics: _*),
        ^.renderItem := { data: FlatListData[TopicWithInfoData] =>
          renderItem(data.item)
        },
        ^.keyExtractor := { item: TopicWithInfoData =>
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

  private[topic] lazy val styles = StyleSheet.create(new Styles)
  private[topic] class Styles extends js.Object {
    import Style._

    val container: Style = new ViewStyle {
      override val flex = 1
    }
    val itemContainer: Style = new ViewStyle {
      override val padding = 10
      override val borderBottomWidth = 2
      override val borderBottomColor = Color.darkgray
    }
    val itemTitle: Style = new TextStyle {
      override val fontSize = 20
    }
    val itemDescription: Style = new TextStyle {
      override val color = "rgba(0, 0, 0, .5)"
    }
  }
}
