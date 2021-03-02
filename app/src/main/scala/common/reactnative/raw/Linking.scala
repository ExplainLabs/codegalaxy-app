package common.reactnative.raw

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("react-native", "Linking")
object Linking extends Linking

@js.native
trait Linking extends js.Object {

  def openURL(url: String): js.Promise[Unit] = js.native
}
