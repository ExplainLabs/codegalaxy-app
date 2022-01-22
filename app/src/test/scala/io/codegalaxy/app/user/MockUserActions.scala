package io.codegalaxy.app.user

import io.codegalaxy.api.user.UserApi
import io.codegalaxy.app.config.ConfigActions.ConfigUpdateAction
import io.codegalaxy.app.config.{ConfigActions, ConfigService}
import io.codegalaxy.app.user.UserActions._
import scommons.react.redux.Dispatch
import scommons.reactnative.Image

//noinspection NotImplementedCode
class MockUserActions(
  userLoginMock: (Dispatch, String, String) => UserLoginAction = (_, _, _) => ???,
  userSignupMock: () => UserSignupAction = () => ???,
  userProfileFetchMock: Dispatch => UserLoginAction = _ => ???,
  userLogoutMock: Dispatch => UserLogoutAction = _ => ???,
  updateDarkThemeMock: (Dispatch, Int, Boolean) => ConfigUpdateAction = (_, _, _) => ???
) extends UserActions with ConfigActions {

  override protected def client: UserApi = ???
  override protected def userService: UserService = ???
  override protected def configService: ConfigService = ???
  override protected def image: Image = ???
  
  override def userLogin(dispatch: Dispatch, user: String, password: String): UserLoginAction =
    userLoginMock(dispatch, user, password)
    
  override def userSignup(): UserSignupAction = userSignupMock()
  
  override def userProfileFetch(dispatch: Dispatch): UserLoginAction =
    userProfileFetchMock(dispatch)
    
  override def userLogout(dispatch: Dispatch): UserLogoutAction =
    userLogoutMock(dispatch)

  override def updateDarkTheme(dispatch: Dispatch, userId: Int, darkTheme: Boolean): ConfigUpdateAction =
    updateDarkThemeMock(dispatch, userId, darkTheme)
}
