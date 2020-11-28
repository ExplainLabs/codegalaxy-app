package io.codegalaxy.app.user

import io.codegalaxy.app.config.ConfigActions.ConfigUpdatedAction
import io.codegalaxy.app.user.UserActions._
import io.codegalaxy.domain.{ConfigEntity, ProfileEntity}

case class UserState(profile: Option[ProfileEntity] = None,
                     config: Option[ConfigEntity] = None)

object UserStateReducer {

  def apply(state: Option[UserState], action: Any): UserState = {
    reduce(state.getOrElse(UserState()), action)
  }

  private def reduce(state: UserState, action: Any): UserState = action match {
    case UserLoggedinAction(profile, config) => state.copy(
      profile = profile,
      config = config
    )
    case UserLoggedoutAction() => state.copy(
      profile = None,
      config = None
    )
    case ConfigUpdatedAction(config) => state.copy(config = Some(config))
    case _ => state
  }
}
