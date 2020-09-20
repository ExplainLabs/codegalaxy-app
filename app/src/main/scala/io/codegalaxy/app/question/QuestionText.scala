package io.codegalaxy.app.question

import scommons.react._
import scommons.reactnative._
import scommons.reactnative.highlighter._
import scommons.reactnative.htmlview._

import scala.scalajs.js

case class QuestionTextProps(textHtml: String,
                             style: Option[Style] = None)

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
        val idx = siblings.indexOf(node)
        val newLineBefore = {
          (idx - 1) >= 0 && siblings(idx - 1).data.getOrElse("").trim.nonEmpty
        }
        val newLineAfter = {
          (idx + 1) < siblings.length && siblings(idx + 1).data.getOrElse("").trim.nonEmpty
        }
        
        node.children.find(_.name.getOrElse("") == "code")
          .flatMap(getCodeData).map((newLineBefore, newLineAfter, _))
      }
      else if (tagName == "code") getCodeData(node).map((false, false, _))
      else None
    
    codeData match {
      case Some((newLineBefore, newLineAfter, (code, language))) =>
        <.>(^.key := s"$index")(
          if (newLineBefore) Some("\n\n")
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
          
          if (newLineAfter) Some("\n\n")
          else None
        )
      case _ if node.`type` == "text" =>
        val text = {
          // trim line breaks at the beginning and at the end of text
          val text = node.data.getOrElse("")
          text.slice(
            text.indexWhere(_ != '\n'),
            text.lastIndexWhere(_ != '\n') + 1
          )
        }
        
        <.Text(^.key := s"$index")(entities.decodeHTML(text))
      case _ => ()
    }
  }
  
  protected def render(compProps: Props): ReactElement = {
    val props = compProps.wrapped
    
    // normalize line breaks
    val text = brTagRegex.replaceAllIn(props.textHtml.trim, "\n")
    
    <.HTMLView(
      props.style.map(^.rnStyle := _),
      ^.renderNode := renderNode,
      ^.value := s"<div>$text</div>"
    )()
  }
  
  private lazy val brTagRegex = """\s*<br>\s*""".r

  private[question] val codeBlockStyle = new js.Object {
    val margin = 0
    val padding = 0
  }
}
