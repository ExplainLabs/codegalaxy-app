package io.codegalaxy.app.auth

import io.codegalaxy.app.user.{UserActions, UserState}
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react._
import scommons.react.hooks._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

case class AuthScreenProps(dispatch: Dispatch,
                           actions: UserActions,
                           state: UserState,
                           onAppReady: () => Unit,
                           onSuccessfulLogin: () => Unit)

object AuthScreen extends FunctionComponent[AuthScreenProps] {

  protected def render(compProps: Props): ReactElement = {
    val (showLoading, setShowLoading) = useState(true)
    val props = compProps.wrapped
    val showLogin = props.state.profile.isEmpty

    useEffect({ () =>
      val action = props.actions.userProfileFetch(props.dispatch)
      props.dispatch(action)

      action.task.future.andThen { case _ =>
        props.onAppReady()
        setShowLoading(false)
      }
      ()
    }, Nil)

    <.>()(
      if (!showLoading && showLogin) Some(
        <(LoginScreen())(^.wrapped := LoginScreenProps(onLogin = { (email, password) =>
          val action = props.actions.userAuth(props.dispatch, email, password)
          props.dispatch(action)
          
          action.task.future.andThen {
            case Success(_) => props.onSuccessfulLogin()
          }
        }))()
      )
      else None
    )
  }
}
