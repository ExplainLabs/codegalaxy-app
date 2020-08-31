package io.codegalaxy.app.user

import io.codegalaxy.app.user.UserActions._
import io.codegalaxy.domain.ProfileEntity

case class UserState(profile: Option[ProfileEntity] = None)

object UserStateReducer {

  def apply(state: Option[UserState], action: Any): UserState = {
    reduce(state.getOrElse(UserState()), action)
  }

  private def reduce(state: UserState, action: Any): UserState = action match {
    case UserLoggedinAction(login) => state.copy(profile = login)
    case UserLoggedoutAction() => state.copy(profile = None)
    case _ => state
  }
}
