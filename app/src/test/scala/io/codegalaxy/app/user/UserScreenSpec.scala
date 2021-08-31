package io.codegalaxy.app.user

import io.codegalaxy.app.config.ConfigActions
import io.codegalaxy.app.config.ConfigActions.ConfigUpdateAction
import io.codegalaxy.app.user.UserActions.UserLogoutAction
import io.codegalaxy.app.user.UserScreen._
import io.codegalaxy.app.user.UserScreenSpec.UserAndConfigActions
import io.codegalaxy.domain.{ConfigEntity, ProfileEntity}
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react._
import scommons.react.navigation._
import scommons.react.redux.task.FutureTask
import scommons.react.test._
import scommons.reactnative.ScrollView._
import scommons.reactnative.Switch._
import scommons.reactnative._

import scala.concurrent.Future
import scala.scalajs.js

class UserScreenSpec extends TestSpec with TestRendererUtils {

  it should "dispatch actions on logout" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserAndConfigActions]
    val props = getUserScreenProps(dispatch, actions)
    val comp = testRender(<(UserScreen())(^.wrapped := props)())
    val logout = findComponents(comp, <.Text.reactClass).last

    val loguotAction = UserLogoutAction(
      FutureTask("Fetching User", Future.successful(()))
    )
    (actions.userLogout _).expects(dispatch).returning(loguotAction)

    //then
    dispatch.expects(loguotAction)

    //when
    logout.props.onPress()
  }

  it should "dispatch actions on darkTheme config update" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserAndConfigActions]
    val props = getUserScreenProps(dispatch, actions)
    val profile = inside(props.data.profile) {
      case Some(profile) => profile
    }
    val comp = testRender(<(UserScreen())(^.wrapped := props)())
    val switch = inside(findComponents(comp, <.Switch.reactClass)) {
      case List(switch) => switch
    }
    val config = ConfigEntity(profile.id, darkTheme = true)

    val updateAction = ConfigUpdateAction(
      FutureTask("Updating Config", Future.successful(config))
    )
    (actions.updateDarkTheme _).expects(dispatch, profile.id, true).returning(updateAction)

    //then
    dispatch.expects(updateAction)

    //when
    switch.props.onValueChange(true)
  }

  it should "render empty component if no profile data" in {
    //given
    val props = {
      val props = getUserScreenProps()
      props.copy(data = props.data.copy(profile = None))
    }

    //when
    val result = testRender(<(UserScreen())(^.wrapped := props)())

    //then
    assertNativeComponent(result,
      <.ScrollView(^.keyboardShouldPersistTaps := KeyboardShouldPersistTaps.always)(
        <.View(^.rnStyle := styles.cardContainer)()
      )
    )
  }
  
  it should "render profile data" in {
    //given
    val props = getUserScreenProps()

    //when
    val result = testRender(<(UserScreen())(^.wrapped := props)())

    //then
    assertUserScreen(result, props)
  }
  
  it should "render darkTheme config ON" in {
    //given
    val props = {
      val props = getUserScreenProps()
      props.copy(data = props.data.copy(config = Some(ConfigEntity(123, darkTheme = true))))
    }

    //when
    val result = testRender(<(UserScreen())(^.wrapped := props)())

    //then
    assertUserScreen(result, props)
  }
  
  private def getUserScreenProps(dispatch: Dispatch = mock[Dispatch],
                                 actions: UserActions with ConfigActions = mock[UserAndConfigActions],
                                 data: UserState = UserState(
                                   profile = Some(ProfileEntity(
                                     id = 123,
                                     username = "test_username",
                                     email = Some("Test email"),
                                     firstName = Some("Test firstName"),
                                     lastName = Some("Test lastName"),
                                     fullName = Some("Test fullName"),
                                     city = Some("Test City"),
                                     avatarUrl = Some("Test avatarUrl")
                                   ))
                                 )): UserScreenProps = {
    UserScreenProps(
      dispatch = dispatch,
      actions = actions,
      data = data
    )
  }
  
  private def assertUserScreen(result: TestInstance, props: UserScreenProps): Unit = {
    implicit val theme: Theme = DefaultTheme
    
    def renderField(name: String, value: Option[String]): ReactElement = {
      <.View(^.rnStyle := styles.fieldRow)(
        <.View(^.rnStyle := styles.fieldContainer)(
          <.Text(themeStyle(styles.fieldName, themeTextStyle))(s"$name:")
        ),
        <.View(^.rnStyle := styles.valueContainer)(
          <.Text(themeStyle(styles.fieldValue, themeTextStyle))(s"${value.getOrElse("")}")
        )
      )
    }

    def renderSwitch(name: String, value: Boolean): ReactElement = {
      <.View(^.rnStyle := styles.fieldRow)(
        <.View(^.rnStyle := styles.fieldContainer)(
          <.Text(themeStyle(styles.fieldName, themeTextStyle))(s"$name:")
        ),
        <.View(^.rnStyle := styles.valueContainer)(
          <.Switch(
            ^.trackColor := new TrackColor {
              val `false`: String = "#767577"
              val `true`: String = "#81b0ff"
            },
            ^.thumbColor := {
              if (value) "#f5dd4b" else "#f4f3f4"
            },
            ^("ios_backgroundColor") := "#3e3e3e",
            ^.switchValue := value
          )()
        )
      )
    }
    
    val profile = inside(props.data.profile) {
      case Some(profile) => profile
    }
    
    assertNativeComponent(result,
      <.ScrollView(^.keyboardShouldPersistTaps := KeyboardShouldPersistTaps.always)(
        <.View(^.rnStyle := styles.cardContainer)(
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
          renderSwitch("Dark Theme", value = props.data.config.exists(_.darkTheme)),
  
          <.Text(^.rnStyle := styles.logoutBtn)("Logout")
        )
      )
    )
  }
}

object UserScreenSpec {

  private trait UserAndConfigActions
    extends UserActions
      with ConfigActions
}
