package io.codegalaxy.app.question

import io.codegalaxy.app.question.QuestionText._
import io.codegalaxy.app.question.QuestionTextSpec._
import scommons.react._
import scommons.react.navigation._
import scommons.react.test._
import scommons.reactnative._
import scommons.reactnative.highlighter._
import scommons.reactnative.htmlview._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportAll

class QuestionTextSpec extends TestSpec with TestRendererUtils {

  private implicit val theme: Theme = DefaultTheme

  it should "return SyntaxHighlighter for pre.code tag with line breaks when renderNode" in {
    //given
    val siblingNode1 = mock[HTMLViewNodeMock]
    val siblingNode2 = mock[HTMLViewNodeMock]
    val preNode = mock[HTMLViewNodeMock]
    val codeNode = mock[HTMLViewNodeMock]
    val textNode = mock[HTMLViewNodeMock]
    val attribs = js.Dynamic.literal(
      "class" -> "java"
    )
    val code = "some java code"
    
    (siblingNode1.data _).expects().returning("siblingNode1 text")
    (siblingNode2.data _).expects().returning("siblingNode2 text")
    (preNode.name _).expects().returning("pre")
    (preNode.children _).expects().returning(js.Array(codeNode.asInstanceOf[HTMLViewNode]))
    (codeNode.name _).expects().returning("code")
    (codeNode.attribs _).expects().returning(attribs)
    (codeNode.children _).expects().returning(js.Array(textNode.asInstanceOf[HTMLViewNode]))
    (textNode.data _).expects().returning(code)
    
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
    val preNode = mock[HTMLViewNodeMock]
    val codeNode = mock[HTMLViewNodeMock]
    val textNode = mock[HTMLViewNodeMock]
    val attribs = js.Dynamic.literal()
    val code = "some test code"
    
    (preNode.name _).expects().returning("pre")
    (preNode.children _).expects().returning(js.Array(codeNode.asInstanceOf[HTMLViewNode]))
    (codeNode.name _).expects().returning("code")
    (codeNode.attribs _).expects().returning(attribs)
    (codeNode.children _).expects().returning(js.Array(textNode.asInstanceOf[HTMLViewNode]))
    (textNode.data _).expects().returning(s" $code\n")
    
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
    val codeNode = mock[HTMLViewNodeMock]
    val textNode = mock[HTMLViewNodeMock]
    val attribs = js.Dynamic.literal(
      "class" -> "scala"
    )
    val code = "some scala code"
    
    (codeNode.name _).expects().returning("code")
    (codeNode.attribs _).expects().returning(attribs)
    (codeNode.children _).expects().returning(js.Array(textNode.asInstanceOf[HTMLViewNode]))
    (textNode.data _).expects().returning(s" $code\n")
    
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
    val codeNode = mock[HTMLViewNodeMock]
    val textNode = mock[HTMLViewNodeMock]
    val attribs = js.Dynamic.literal(
      "class" -> "scala"
    )
    val code = "some scala code"
    
    (codeNode.name _).expects().returning("code")
    (codeNode.attribs _).expects().returning(attribs)
    (codeNode.children _).expects().returning(js.Array(textNode.asInstanceOf[HTMLViewNode]))
    (textNode.data _).expects().returning(s" $code\n")
    
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
    val node = mock[HTMLViewNodeMock]
    
    (node.name _).expects().returning("div")
    
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
    createTestRenderer(resultComp.asInstanceOf[ReactElement]).root
  }
}

object QuestionTextSpec {

  @JSExportAll
  trait HTMLViewNodeMock {

    def name: js.UndefOr[String]
    def data: js.UndefOr[String]
    def attribs: js.Dynamic
    def children: js.Array[HTMLViewNode]
  }
}
