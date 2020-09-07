package io.codegalaxy.app.user

import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react._
import scommons.reactnative._

import scala.scalajs.js

case class UserScreenProps(dispatch: Dispatch,
                           actions: UserActions,
                           data: UserState)

object UserScreen extends FunctionComponent[UserScreenProps] {
  
  protected def render(compProps: Props): ReactElement = {
    val props = compProps.wrapped

    def renderField(name: String, value: Option[String]): ReactElement = {
      <.View(^.rnStyle := styles.fieldRow)(
        <.View(^.rnStyle := styles.fieldContainer)(
          <.Text(^.rnStyle := styles.fieldName)(s"$name:")
        ),
        <.View(^.rnStyle := styles.valueContainer)(
          <.Text(^.rnStyle := styles.fieldValue)(s"${value.getOrElse("")}")
        )
      )
    }
    
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

          <.Text(^.rnStyle := styles.logoutBtn, ^.onPress := { () =>
            props.dispatch(props.actions.userLogout(props.dispatch))
          })("Logout")
        )
      }
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
      override val marginTop = 20
    }
  }
}
