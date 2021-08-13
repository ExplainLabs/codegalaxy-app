package io.codegalaxy.app.question

import scommons.react._
import scommons.react.navigation._
import scommons.reactnative._
import scommons.reactnative.highlighter._
import scommons.reactnative.htmlview._

import scala.scalajs.js

case class QuestionTextProps(textHtml: String,
                             style: Option[js.Array[Style]] = None)

object QuestionText extends FunctionComponent[QuestionTextProps] {

  def renderNode(dark: Boolean)(node: HTMLViewNode,
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
          if (newLineBefore) Some("\n")
          else None,
          
          <.SyntaxHighlighter(
            ^.PreTag := <.Text.reactClass,
            ^.CodeTag := <.Text.reactClass,
            language.map(^.language := _),
            ^.customStyle := codeBlockStyle,
            ^.highlighter := "hljs",
            ^.highlighterStyle := {
              val hlStyle =
                if (dark) getHighlightJsStyle("dark")
                else getHighlightJsStyle("github")
              
              hlStyle.getOrElse(HighlightJsStyles.defaultStyle)
            }
          )(entities.decodeHTML(code.trim)),
          
          if (newLineAfter) Some("\n")
          else None
        )
      case _ => ()
    }
  }
  
  protected def render(compProps: Props): ReactElement = {
    implicit val theme: Theme = useTheme()
    val props = compProps.wrapped
    
    val text = props.textHtml.trim
    
    val textProps = ^.textComponentProps := {
      val attrs = new js.Object {
        val style = themeTextStyle
      }
      attrs
    }

    <.HTMLView(
      props.style.map(^.rnStyle := _),
      textProps,
      ^.renderNode := renderNode(theme.dark) _,
      ^.value := s"<div>$text</div>"
    )()
  }
  
  private[question] val codeBlockStyle = new js.Object {
    val margin = 0
    val padding = 0
  }
}
