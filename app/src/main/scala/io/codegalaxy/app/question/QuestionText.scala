package io.codegalaxy.app.question

import scommons.react._
import scommons.reactnative._
import scommons.reactnative.highlighter._
import scommons.reactnative.htmlview._

import scala.scalajs.js

case class QuestionTextProps(textHtml: String)

object QuestionText extends FunctionComponent[QuestionTextProps] {

  def renderNode(node: HTMLViewNode,
                 index: Int,
                 siblings: js.Array[HTMLViewNode],
                 parent: js.UndefOr[HTMLViewNode],
                 defaultRenderer: DefaultRendererFn): js.Any = {

    def getCodeData(node: HTMLViewNode): Option[(String, Option[String])] = {
      node.children.headOption.flatMap(_.data.toOption).map { code =>
        (code, node.attribs.selectDynamic("class").asInstanceOf[js.UndefOr[String]].toOption)
      }
    }
    
    val tagName = node.name.getOrElse("")
    val codeData =
      if (tagName == "pre") {
        val preIndex = siblings.indexOf(node)
        val newLineBefore = preIndex > 0
        val newLineAfter = preIndex < (siblings.length - 1)
        
        node.children.find(_.name.getOrElse("") == "code")
          .flatMap(getCodeData).map((newLineBefore, newLineAfter, _))
      }
      else if (tagName == "code") getCodeData(node).map((false, false, _))
      else None
    
    codeData match {
      case Some((newLineBefore, newLineAfter, (code, language))) =>
        <.>(^.key := s"$index")(
          if (newLineBefore) Some("\n")
          else None,
          
          <.SyntaxHighlighter(
            ^.PreTag := <.Text.reactClass,
            ^.CodeTag := <.Text.reactClass,
            language.map(^.language := _),
            ^.customStyle := codeBlockStyle,
            ^.highlighter := "hljs",
            ^.highlighterStyle := getHighlightJsStyle("github")
              .getOrElse(HighlightJsStyles.defaultStyle)
          )(entities.decodeHTML(code.trim)),
          
          if (newLineAfter) Some("\n")
          else None
        )
      case _ => ()
    }
  }
  
  protected def render(compProps: Props): ReactElement = {
    val props = compProps.wrapped
    
    <.HTMLView(
      ^.rnStyle := styles.container,
      ^.renderNode := renderNode,
      ^.value := s"<div>${props.textHtml}</div>"
    )()
  }

  private[question] val codeBlockStyle = new js.Object {
    val margin = 0
    val padding = 0
  }

  private[question] lazy val styles = StyleSheet.create(new Styles)
  private[question] class Styles extends js.Object {

    val container: Style = new ViewStyle {
      override val margin = 5
      override val padding = 5
    }
  }
}
