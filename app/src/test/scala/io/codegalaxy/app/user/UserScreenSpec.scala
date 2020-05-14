package io.codegalaxy.app.user

import io.codegalaxy.api.user.{UserData, UserProfileData}
import io.codegalaxy.app.user.UserScreen._
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react._
import scommons.react.navigation._
import scommons.react.test._
import scommons.reactnative._

import scala.scalajs.js

class UserScreenSpec extends TestSpec with ShallowRendererUtils {

  it should "render userStackComp" in {
    //given
    val userController = mock[UserController]
    val userControllerComp = "UserController".asInstanceOf[ReactClass]
    (userController.apply _).expects().returning(userControllerComp)
    val userStackComp = UserScreen.userStackComp(userController)

    //when
    val result = shallowRender(<(userStackComp)()())

    //then
    assertNativeComponent(result,
      <(Stack.Navigator)(^.initialRouteName := "Profile")(
        <(Stack.Screen)(^.name := "Profile", ^.component := userControllerComp)()
      )
    )
  }

  it should "render empty component if no profile data" in {
    //given
    val props = {
      val props = getUserScreenProps()
      props.copy(data = props.data.copy(login = None))
    }

    //when
    val result = shallowRender(<(UserScreen())(^.wrapped := props)())

    //then
    assertNativeComponent(result, <.View(^.rnStyle := styles.cardContainer)())
  }
  
  it should "render profile data" in {
    //given
    val props = getUserScreenProps()

    //when
    val result = shallowRender(<(UserScreen())(^.wrapped := props)())

    //then
    assertUserScreen(result, props)
  }
  
  private def getUserScreenProps(dispatch: Dispatch = mock[Dispatch],
                                 actions: UserActions = mock[UserActions],
                                 data: UserState = UserState(
                                   login = Some(UserLoginState(
                                     profile = UserProfileData(
                                       userId = 123,
                                       username = "test_username",
                                       city = Some("Test City"),
                                       firstName = Some("Test firstName"),
                                       lastName = Some("Test lastName")
                                     ),
                                     user = UserData(
                                       username = "test_username",
                                       email = Some("Test email"),
                                       fullName = Some("Test fullName"),
                                       avatarUrl = Some("Test avatarUrl")
                                     )
                                   ))
                                 )): UserScreenProps = {
    UserScreenProps(
      dispatch = dispatch,
      actions = actions,
      data = data
    )
  }
  
  private def assertUserScreen(result: ShallowInstance, props: UserScreenProps): Unit = {
    
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
    
    val Some(UserLoginState(profile, user)) = props.data.login
    
    assertNativeComponent(result, <.View(^.rnStyle := styles.cardContainer)(
      <.>()(
        <.View(^.rnStyle := js.Array(styles.cardImageContainer, styles.cardImage, styles.cardImageContainerShadow))(
          user.avatarUrl.map { avatarUrl =>
            <.Image(^.rnStyle := styles.cardImage, ^.source := new UriResource {
              override val uri = avatarUrl
            })()
          }
        ),

        renderField("Full Name", user.fullName),
        renderField("Email", user.email),
        renderField("User Name", Some(profile.username)),
        renderField("City", profile.city)
      )
    ))
  }
}
