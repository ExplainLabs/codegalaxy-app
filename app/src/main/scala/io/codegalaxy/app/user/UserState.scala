package io.codegalaxy.app.user

import io.codegalaxy.api.user._
import io.codegalaxy.app.user.UserActions._

case class UserLoginState(profile: UserProfileData,
                          user: UserData)

case class UserState(login: Option[UserLoginState] = None)

object UserStateReducer {

  def apply(state: Option[UserState], action: Any): UserState = {
    reduce(state.getOrElse(UserState()), action)
  }

  private def reduce(state: UserState, action: Any): UserState = action match {
    case UserLoggedinAction(login) => state.copy(login = login)
    case UserLoggedoutAction() => state.copy(login = None)
    case _ => state
  }
}
