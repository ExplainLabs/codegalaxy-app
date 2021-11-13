package io.codegalaxy.app.user

import io.codegalaxy.app.config.ConfigActions
import scommons.react._
import scommons.react.navigation._
import scommons.react.redux.Dispatch
import scommons.reactnative.ScrollView._
import scommons.reactnative.Switch._
import scommons.reactnative._

import scala.scalajs.js

case class UserScreenProps(dispatch: Dispatch,
                           actions: UserActions with ConfigActions,
                           data: UserState)

object UserScreen extends FunctionComponent[UserScreenProps] {
  
  protected def render(compProps: Props): ReactElement = {
    implicit val theme: Theme = useTheme()
    val props = compProps.wrapped

    def renderField(name: String, value: Option[String]): ReactElement = {
      renderRow(name, <.Text(themeStyle(styles.fieldValue, themeTextStyle))(
        s"${value.getOrElse("")}"
      ))
    }

    def renderSwitch(name: String, value: Boolean, onChange: Boolean => Unit): ReactElement = {
      renderRow(name, <.Switch(
        ^.trackColor := new TrackColor {
          val `false`: String = "#767577"
          val `true`: String = "#81b0ff"
        },
        ^.thumbColor := {
          if (value) "#f5dd4b" else "#f4f3f4"
        },
        ^("ios_backgroundColor") := "#3e3e3e",
        ^.switchOnValueChange := onChange,
        ^.switchValue := value
      )())
    }

    def renderRow(name: String, value: ReactElement): ReactElement = {
      <.View(^.rnStyle := styles.fieldRow)(
        <.View(^.rnStyle := styles.fieldContainer)(
          <.Text(themeStyle(styles.fieldName, themeTextStyle))(s"$name:")
        ),
        <.View(^.rnStyle := styles.valueContainer)(
          value
        )
      )
    }
    
    <.ScrollView(^.keyboardShouldPersistTaps := KeyboardShouldPersistTaps.always)(
      <.View(^.rnStyle := styles.cardContainer)(
        props.data.profile.map { profile =>
          <.>()(
            <.View(^.rnStyle := js.Array(styles.cardImageContainer, styles.cardImage, styles.cardImageContainerShadow))(
              profile.avatarUrl.map { avatarUrl =>
                <.Image(^.rnStyle := styles.cardImage, ^.source := new UriResource {
                  override val uri = avatarUrl
                })()
              }
            ),
  
            renderField("Full Name", profile.fullName),
            renderField("Email", profile.email),
            renderField("User Name", Some(profile.username)),
            renderField("City", profile.city),
  
            <.Text(themeStyle(styles.settings, themeTextStyle))("Settings:"),
            renderSwitch("Dark Theme", props.data.config.exists(_.darkTheme), { value =>
              props.dispatch(props.actions.updateDarkTheme(props.dispatch, profile.id, value))
            }),
  
            <.Text(^.rnStyle := styles.logoutBtn, ^.onPress := { () =>
              props.dispatch(props.actions.userLogout(props.dispatch))
            })("Logout")
          )
        }
      )
    )
  }

  private[user] lazy val styles = StyleSheet.create(new Styles)
  private[user] class Styles extends js.Object {
    import Style._
    import TextStyle._
    import ViewStyle._

    val cardContainer: Style = new ViewStyle {
      override val alignItems = AlignItems.center
    }
    val cardImageContainer: Style = new ViewStyle {
      override val alignItems = AlignItems.center
      override val backgroundColor = Color.white
      override val borderColor = Color.black
      override val borderWidth = 3
      override val marginTop = 30
      override val marginBottom = 20
    }
    val cardImageContainerShadow: Style = Platform.select {
      case Platform.ios | Platform.web => new ViewStyle {
        override val shadowColor = Color.black
        override val shadowOffset = new ShadowOffset {
          override val height = 10
        }
        override val shadowOpacity = 1
      }
      case Platform.android => new ViewStyle {
        override val borderWidth = 3
        override val borderColor = Color.black
        override val elevation = 15
      }
    }
    val cardImage: Style = new ViewStyle {
      override val width = 120
      override val height = 120
      override val borderRadius = 60
    }
    val fieldRow: Style = new ViewStyle {
      override val flexDirection = FlexDirection.row
      override val alignItems = AlignItems.center
      override val marginTop = 10
    }
    val fieldContainer: Style = new ViewStyle {
      override val flex = 2
      override val alignItems = AlignItems.`flex-end`
    }
    val valueContainer: Style = new ViewStyle {
      override val flex = 3
      override val alignItems = AlignItems.`flex-start`
    }
    val fieldName: Style = new TextStyle {
      override val fontWeight = FontWeight.bold
      override val marginRight = 10
    }
    val fieldValue: Style = new TextStyle {
      override val fontStyle = FontStyle.italic
    }
    val logoutBtn: Style = new TextStyle {
      override val color = Color.royalblue
      override val fontSize = 18
      override val fontWeight = FontWeight.bold
      override val marginTop = 50
    }
    val settings: Style = new TextStyle {
      override val marginTop = 20
      override val fontWeight = FontWeight.bold
    }
  }
}
