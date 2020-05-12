package io.codegalaxy.app

import io.codegalaxy.app.user.UserController
import io.github.shogowada.scalajs.reactjs.redux.ReactRedux._
import io.github.shogowada.scalajs.reactjs.redux.Redux
import scommons.expo.Font
import scommons.react._
import scommons.react.hooks._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel(name = "CodeGalaxyApp")
class CodeGalaxyApp(onReady: js.Function0[Unit]) extends FunctionComponent[Unit] {

  @JSExport("apply")
  override def apply(): ReactClass = super.apply()

  private val store = Redux.createStore(CodeGalaxyStateReducer.reduce)

  private lazy val actions = CodeGalaxyActions
  private lazy val userController = new UserController(actions)
  private lazy val rootComp = new CodeGalaxyRootController(onReady, actions, userController).apply()
  
  protected def render(props: Props): ReactElement = {
    val (isReady, setIsReady) = useState(false)

    useEffect({ () =>
      preloadAssets { () =>
        setIsReady(true)
      }
    }, Nil)

    if (!isReady) <.>()() //Loading...
    else {
      <.Provider(^.store := store)(
        <.>()(
          <(rootComp).empty,
          <(CodeGalaxyTaskController()).empty
        )
      )
    }
  }

  private def preloadAssets(onFinish: () => Unit): Unit = {

    def cacheIcons(): Future[js.Any] = {
      Font.loadAsync(CodeGalaxyIcons.iconsToPreload).recover {
        case error => Console.err.println(s"Failed to cache icons, error: $error")
      }
    }

    Future.sequence(Seq(
      cacheIcons()
    )).andThen { case _ =>
      onFinish()
    }
  }
}
