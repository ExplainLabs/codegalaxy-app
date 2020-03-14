package io.codegalaxy.app.auth

import io.codegalaxy.app.user.UserActions
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react._
import scommons.react.hooks._

import scala.concurrent.ExecutionContext.Implicits.global

case class WithAutoLoginProps(dispatch: Dispatch,
                              actions: UserActions,
                              onReady: () => Unit)

object WithAutoLogin extends FunctionComponent[WithAutoLoginProps] {

  protected def render(compProps: Props): ReactElement = {
    val (isReady, setIsReady) = useState(false)
    val props = compProps.wrapped

    useEffect({ () =>
      val action = props.actions.userProfileFetch(props.dispatch)
      props.dispatch(action)

      action.task.future.andThen { case _ =>
        props.onReady()
        setIsReady(true)
      }
      ()
    }, Nil)

    if (isReady) compProps.children
    else <.>()()
  }
}
