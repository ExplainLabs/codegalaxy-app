package io.codegalaxy.app.user

import io.codegalaxy.api.user.UserProfileData
import io.codegalaxy.app.user.UserActions.UserProfileFetchedAction

case class UserState(profile: Option[UserProfileData] = None)

object UserStateReducer {

  def apply(state: Option[UserState], action: Any): UserState = {
    reduce(state.getOrElse(UserState()), action)
  }

  private def reduce(state: UserState, action: Any): UserState = action match {
    case UserProfileFetchedAction(profile) => state.copy(
      profile = profile
    )
    case _ => state
  }
}
