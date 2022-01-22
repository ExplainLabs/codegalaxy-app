package io.codegalaxy.app.question

import io.codegalaxy.app.question.QuestionText._
import scommons.react._
import scommons.react.navigation._
import scommons.react.test._
import scommons.reactnative._
import scommons.reactnative.highlighter._
import scommons.reactnative.htmlview._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal

class QuestionTextSpec extends TestSpec with TestRendererUtils {

  private implicit val theme: Theme = DefaultTheme

  it should "return SyntaxHighlighter for pre.code tag with line breaks when renderNode" in {
    //given
    val siblingNode1 = literal("data" -> "siblingNode1 text")
    val siblingNode2 = literal("data" -> "siblingNode2 text")
    val attribs = literal("class" -> "java")
    val code = "some java code"
    val textNode = literal("data" -> code)
    val codeNode = literal("name" -> "code", "attribs" -> attribs,
      "children" -> js.Array(textNode)
    )
    val preNode = literal("name" -> "pre", "children" -> js.Array(codeNode))
    
    //when
    val result = QuestionText.renderNode(theme.dark)(
      node = preNode.asInstanceOf[HTMLViewNode],
      index = 1,
      siblings = js.Array(siblingNode1, preNode, siblingNode2).map(_.asInstanceOf[HTMLViewNode]),
      parent = js.undefined,
      defaultRenderer = null
    )
    
    //then
    inside(renderNodeResult(result).children.toList) {
      case List(lineBreakBefore, syntaxHighlighter, lineBreakAfter) =>
        lineBreakBefore shouldBe "\n"
        lineBreakAfter shouldBe "\n"
        assertNativeComponent(syntaxHighlighter,
          <.SyntaxHighlighter(
            ^.PreTag := <.Text.reactClass,
            ^.CodeTag := <.Text.reactClass,
            ^.language := "java",
            ^.customStyle := codeBlockStyle,
            ^.highlighter := "hljs",
            ^.highlighterStyle := getHighlightJsStyle("github")
              .getOrElse(HighlightJsStyles.defaultStyle)
          )(code.trim)
        )
    }
  }
  
  it should "return SyntaxHighlighter for pre.code tag without line breaks when renderNode" in {
    //given
    val attribs = literal()
    val code = "some test code"
    val textNode = literal("data" -> s" $code\n")
    val codeNode = literal("name" -> "code", "attribs" -> attribs,
      "children" -> js.Array(textNode)
    )
    val preNode = literal("name" -> "pre", "children" -> js.Array(codeNode))
    
    //when
    val result = QuestionText.renderNode(theme.dark)(
      node = preNode.asInstanceOf[HTMLViewNode],
      index = 1,
      siblings = js.Array(preNode).map(_.asInstanceOf[HTMLViewNode]),
      parent = js.undefined,
      defaultRenderer = null
    )
    
    //then
    assertNativeComponent(renderNodeResult(result),
      <.SyntaxHighlighter(
        ^.PreTag := <.Text.reactClass,
        ^.CodeTag := <.Text.reactClass,
        ^.customStyle := codeBlockStyle,
        ^.highlighter := "hljs",
        ^.highlighterStyle := getHighlightJsStyle("github")
          .getOrElse(HighlightJsStyles.defaultStyle)
      )(code.trim)
    )
  }
  
  it should "return SyntaxHighlighter for code tag when renderNode" in {
    //given
    val attribs = literal("class" -> "scala")
    val code = "some scala code"
    val textNode = literal("data" -> s" $code\n")
    val codeNode = literal("name" -> "code", "attribs" -> attribs,
      "children" -> js.Array(textNode)
    )
    
    //when
    val result = QuestionText.renderNode(theme.dark)(
      node = codeNode.asInstanceOf[HTMLViewNode],
      index = 1,
      siblings = js.Array[HTMLViewNode](),
      parent = js.undefined,
      defaultRenderer = null
    )
    
    //then
    assertNativeComponent(renderNodeResult(result),
      <.SyntaxHighlighter(
        ^.PreTag := <.Text.reactClass,
        ^.CodeTag := <.Text.reactClass,
        ^.language := "scala",
        ^.customStyle := codeBlockStyle,
        ^.highlighter := "hljs",
        ^.highlighterStyle := getHighlightJsStyle("github")
          .getOrElse(HighlightJsStyles.defaultStyle)
      )(code.trim)
    )
  }
  
  it should "return SyntaxHighlighter with dark theme when renderNode" in {
    //given
    val attribs = literal("class" -> "scala")
    val code = "some scala code"
    val textNode = literal("data" -> s" $code\n")
    val codeNode = literal("name" -> "code", "attribs" -> attribs,
      "children" -> js.Array(textNode)
    )
    
    //when
    val result = QuestionText.renderNode(dark = true)(
      node = codeNode.asInstanceOf[HTMLViewNode],
      index = 1,
      siblings = js.Array[HTMLViewNode](),
      parent = js.undefined,
      defaultRenderer = null
    )
    
    //then
    assertNativeComponent(renderNodeResult(result),
      <.SyntaxHighlighter(
        ^.PreTag := <.Text.reactClass,
        ^.CodeTag := <.Text.reactClass,
        ^.language := "scala",
        ^.customStyle := codeBlockStyle,
        ^.highlighter := "hljs",
        ^.highlighterStyle := getHighlightJsStyle("dark")
          .getOrElse(HighlightJsStyles.defaultStyle)
      )(code.trim)
    )
  }
  
  it should "return undefined for all other nodes when renderNode" in {
    //given
    val node = literal("name" -> "div")
    
    //when
    val result = QuestionText.renderNode(theme.dark)(
      node = node.asInstanceOf[HTMLViewNode],
      index = 0,
      siblings = js.Array[HTMLViewNode](),
      parent = js.undefined,
      defaultRenderer = null
    )
    
    //then
    result shouldBe js.undefined
  }
  
  it should "render component with normalized html" in {
    //given
    val style = js.Array(new Style {})
    val props = QuestionTextProps(
      """ <code>1</code>  <code class="java">
        |  if (1 < 2) ...
        |</code> <br> 
        | test
        | """.stripMargin,
      Some(style)
    )
    val component = <(QuestionText())(^.wrapped := props)()
    
    //when
    val result = testRender(component)
    
    //then
    val textProps = ^.textComponentProps := {
      val attrs = new js.Object {
        val style = themeTextStyle
      }
      attrs
    }
    assertNativeComponent(result,
      <.HTMLView(
        ^.rnStyle := style,
        textProps,
        ^.value :=
          """<div><code>1</code>  <code class="java">
            |  if (1 < 2) ...
            |</code> <br> 
            | test</div>""".stripMargin
      )()
    )
  }
  
  private def renderNodeResult(resultComp: Any): TestInstance = {
    resultComp should not be js.undefined
    
    createTestRenderer(resultComp.asInstanceOf[ReactElement]).root
  }
}
