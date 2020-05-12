package io.codegalaxy.app.user

import io.codegalaxy.api.user.UserProfileData
import io.codegalaxy.app.user.UserScreen._
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react._
import scommons.react.navigation._
import scommons.react.test._
import scommons.reactnative._

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
      props.copy(data = props.data.copy(profile = None))
    }

    //when
    val result = shallowRender(<(UserScreen())(^.wrapped := props)())

    //then
    assertNativeComponent(result, <.View()())
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
                                   profile = Some(UserProfileData(
                                     userId = 123,
                                     username = "test_username",
                                     city = Some("Test City"),
                                     firstName = Some("Test firstName"),
                                     lastName = Some("Test lastName")
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
    
    val Some(data) = props.data.profile
    
    assertNativeComponent(result, <.View()(
      <.>()(
        renderField("User Name", Some(data.username)),
        renderField("First Name", data.firstName),
        renderField("Last Name", data.lastName),
        renderField("City", data.city)
      )
    ))
  }
}
