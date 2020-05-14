package io.codegalaxy.app.user

import io.codegalaxy.api.user.{UserData, UserProfileData}
import io.codegalaxy.app.user.UserActions.UserLoggedinAction

case class UserLoginState(profile: UserProfileData,
                          user: UserData)

case class UserState(login: Option[UserLoginState] = None)

object UserStateReducer {

  def apply(state: Option[UserState], action: Any): UserState = {
    reduce(state.getOrElse(UserState()), action)
  }

  private def reduce(state: UserState, action: Any): UserState = action match {
    case UserLoggedinAction(login) => state.copy(
      login = login
    )
    case _ => state
  }
}
