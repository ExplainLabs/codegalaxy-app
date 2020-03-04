package io.codegalaxy.app.popup

import io.codegalaxy.app.CodeGalaxyTheme
import scommons.react._
import scommons.react.hooks._
import scommons.reactnative.Modal._
import scommons.reactnative.TextInput._
import scommons.reactnative._

import scala.scalajs.js

case class LoginPopupProps(onLogin: (String, String) => Unit)

object LoginPopup extends FunctionComponent[LoginPopupProps] {

  protected def render(compProps: Props): ReactElement = {
    val (email, setEmail) = useState("")
    val (password, setPassword) = useState("")
    val props = compProps.wrapped
    val disabled = !email.contains('@') || password.isEmpty

    <.Modal(
      ^.animationType := AnimationType.slide,
      ^.transparent := false,
      ^.visible := true
    )(
      <.View(^.rnStyle := styles.container)(
        <.Text(^.rnStyle := styles.heading)(
          "Welcome to CodeGalaxy"
        ),
  
        <.TextInput(
          ^.placeholder := "E-MAIL-ADDRESS",
          ^.rnStyle := styles.input,
          ^.keyboardType := KeyboardType.`email-address`,
          ^.autoCapitalize := AutoCapitalize.none,
          ^.autoCompleteType := AutoCompleteType.off, // android
          ^.autoCorrect := false,
          ^.value := email,
          ^.onChangeText := setEmail
        )(),
  
        <.TextInput(
          ^.placeholder := "PASSWORD",
          ^.rnStyle := styles.input,
          ^.secureTextEntry := true,
          ^.value := password,
          ^.onChangeText := setPassword
        )(),
  
        <.TouchableOpacity(
          ^.disabled := disabled,
          ^.onPress := { () =>
            props.onLogin(email, password)
          }
        )(
          <.View(^.rnStyle := styles.button)(
            <.Text(^.rnStyle := js.Array(
              styles.buttonText,
              if (disabled) styles.buttonTextDisabled
              else styles.buttonTextEnabled
            ))("Login")
          )
        )
      )
    )
  }

  private[popup] lazy val styles = StyleSheet.create(new Styles)
  private[popup] class Styles extends js.Object {
    import Style._
    import ViewStyle._

    val container: Style = new ViewStyle {
      override val backgroundColor = CodeGalaxyTheme.Colors.primary
      override val flex = 1
      override val justifyContent = JustifyContent.center
    }
    val heading: Style = new ViewStyle with TextStyle {
      override val color = Color.white
      override val fontSize = 40
      override val marginBottom = 10
      override val alignSelf = AlignSelf.center
    }
    val input: Style = new TextStyle {
      override val margin = 10
      override val backgroundColor = Color.white
      override val paddingHorizontal = 8
      override val height = 50
    }
    val button: Style = new ViewStyle {
      override val height = 50
      override val backgroundColor = "#666"
      override val justifyContent = JustifyContent.center
      override val alignItems = AlignItems.center
      override val margin = 10
    }
    val buttonText: Style = new TextStyle {
      override val fontSize = 18
    }
    val buttonTextEnabled: Style = new TextStyle {
      override val color = Color.white
    }
    val buttonTextDisabled: Style = new TextStyle {
      override val color = Color.black
    }
  }
}
