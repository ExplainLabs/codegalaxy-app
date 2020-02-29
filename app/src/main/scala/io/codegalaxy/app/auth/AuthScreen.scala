package io.codegalaxy.app.auth

import io.codegalaxy.app.CodeGalaxyTheme
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react._
import scommons.react.hooks._
import scommons.reactnative.TextInput._
import scommons.reactnative._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.util.Success

case class AuthScreenProps(dispatch: Dispatch,
                           actions: AuthActions,
                           onSuccessfulLogin: () => Unit)

object AuthScreen extends FunctionComponent[AuthScreenProps] {

  protected def render(compProps: Props): ReactElement = {
    val (email, setEmail) = useState("")
    val (password, setPassword) = useState("")
    val disabled = !email.contains('@') || password.isEmpty
    
    val props = compProps.wrapped

    <.View(^.rnStyle := styles.container)(
      <.Text(^.rnStyle := styles.heading)(
        "Welcome to CodeGalaxy"
      ),

      <.TextInput(
        ^.placeholder := "E-MAIL-ADDRESS",
        ^.rnStyle := styles.input,
        ^.keyboardType := KeyboardType.`email-address`,
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
          val action = props.actions.authenticate(props.dispatch, email, password)
          props.dispatch(action)
          
          action.task.future.andThen {
            case Success(_) => props.onSuccessfulLogin()
          }
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
  }

  private[auth] lazy val styles = StyleSheet.create(new Styles)
  private[auth] class Styles extends js.Object {
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
