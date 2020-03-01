package io.codegalaxy.app.auth

import io.codegalaxy.app.popup._
import io.codegalaxy.app.user.{UserActions, UserState}
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react._
import scommons.react.hooks._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

case class AuthScreenProps(dispatch: Dispatch,
                           actions: UserActions,
                           state: UserState,
                           onSuccessfulLogin: () => Unit)

object AuthScreen extends FunctionComponent[AuthScreenProps] {

  protected def render(compProps: Props): ReactElement = {
    val (showLoading, setShowLoading) = useState(true)
    val (showLogin, setShowLogin) = useState(false)
    val props = compProps.wrapped

    useEffect({ () =>
      val action = props.actions.userProfileFetch(props.dispatch)
      props.dispatch(action)

      action.task.future.andThen {
        case _ => setShowLoading(false)
      }
      ()
    }, Nil)

    <.>()(
      if (showLoading) Some(
        <(LoadingPopup()).empty
      )
      else if (showLogin) Some(
        <(LoginPopup())(^.wrapped := LoginPopupProps(onLogin = { (email, password) =>
          val action = props.actions.userAuth(props.dispatch, email, password)
          props.dispatch(action)
          
          action.task.future.andThen {
            case Success(_) =>
              props.onSuccessfulLogin()
              setShowLogin(false)
          }
        }))()
      )
      else None
    )
  }
}
