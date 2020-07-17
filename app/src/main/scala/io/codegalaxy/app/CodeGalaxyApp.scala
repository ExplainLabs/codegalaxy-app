package io.codegalaxy.app

import io.codegalaxy.domain.{CodeGalaxyDBContext, CodeGalaxyDBMigrations}
import io.github.shogowada.scalajs.reactjs.redux.ReactRedux._
import io.github.shogowada.scalajs.reactjs.redux.Redux
import scommons.expo.Font
import scommons.expo.sqlite.SQLite
import scommons.react._
import scommons.react.hooks._
import scommons.websql.Database
import scommons.websql.migrations.WebSqlMigrations

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel(name = "CodeGalaxyApp")
class CodeGalaxyApp(onReady: js.Function0[Unit]) extends FunctionComponent[Unit] {

  @JSExport("apply")
  override def apply(): ReactClass = super.apply()

  private val store = Redux.createStore(CodeGalaxyStateReducer.reduce)

  protected def render(props: Props): ReactElement = {
    val rootCompRef = useRef[CodeGalaxyRootController](null)
    val (isReady, setIsReady) = useState(false)

    useEffect({ () =>
      val dbF = prepareDB()
      val assetsF = preloadAssets()
      val resultF = for {
        db <- dbF
        _ <- assetsF
      } yield {
        val actions = new CodeGalaxyActions(new CodeGalaxyDBContext(db))
        rootCompRef.current = new CodeGalaxyRootController(onReady, actions)
      }

      resultF.andThen { case _ =>
        setIsReady(true)
      }
      ()
    }, Nil)

    if (!isReady) <.>()() //Loading...
    else {
      <.Provider(^.store := store)(
        <.>()(
          if (rootCompRef.current != null) Some(
            <(rootCompRef.current()).empty
          )
          else None,
          <(CodeGalaxyTaskController()).empty
        )
      )
    }
  }

  private def prepareDB(): Future[Database] = {
    val dbF = for {
      db <- SQLite.openDatabase("codegalaxy.db")
      migrations = new WebSqlMigrations(db)
      _ <- migrations.runBundle(CodeGalaxyDBMigrations)
    } yield db
    
    dbF.recover {
      case error =>
        Console.err.println(s"Failed to prepare DB, error: $error")
        throw error
    }
  }
  
  private def preloadAssets(): Future[_] = {

    def cacheIcons(): Future[js.Any] = {
      Font.loadAsync(CodeGalaxyIcons.iconsToPreload).recover {
        case error => Console.err.println(s"Failed to cache icons, error: $error")
      }
    }

    Future.sequence(Seq(
      cacheIcons()
    )).recover {
      case error => Console.err.println(s"Failed to preload assets, error: $error")
    }
  }
}
