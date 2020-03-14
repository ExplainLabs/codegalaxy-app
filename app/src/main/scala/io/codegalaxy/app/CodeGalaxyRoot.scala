package io.codegalaxy.app

import io.codegalaxy.app.auth._
import io.codegalaxy.app.user.{UserActions, UserState}
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react._
import scommons.react.hooks._

import scala.concurrent.ExecutionContext.Implicits.global

case class CodeGalaxyRootProps(dispatch: Dispatch,
                               actions: UserActions,
                               state: UserState,
                               onAppReady: () => Unit)

object CodeGalaxyRoot extends FunctionComponent[CodeGalaxyRootProps] {

  protected def render(compProps: Props): ReactElement = {
    val (appInit, setAppInit) = useState(true)
    val props = compProps.wrapped

    useEffect({ () =>
      val action = props.actions.userProfileFetch(props.dispatch)
      props.dispatch(action)

      action.task.future.andThen { case _ =>
        props.onAppReady()
        setAppInit(false)
      }
      ()
    }, Nil)

    val showLogin = props.state.profile.isEmpty
    
    <.>()(
      if (appInit) None
      else if (showLogin) Some(
        <(LoginScreen())(^.wrapped := LoginScreenProps(onLogin = { (email, password) =>
          props.dispatch(props.actions.userAuth(props.dispatch, email, password))
        }))()
      )
      else None
    )
  }
}
