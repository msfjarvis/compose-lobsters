/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.htmlrenderer

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class InlineAnnotationsTest {
  @Test
  fun `adjacent equal destinations remain distinct anchors and unchanged URLs remain unchanged`() {
    val text =
      (parseHtml("<a href='https://example.com'>A</a><a href='https://example.com'><i>B</i></a>")
          .blocks
          .single() as TextBlock)
        .content
        .annotate(TextLinkStyles())
    val links = text.getLinkAnnotations(0, text.length)
    assertThat(links.map { (it.item as LinkAnnotation.Url).url })
      .containsExactly("https://example.com", "https://example.com")
    assertThat(links.map { it.start to it.end }).containsExactly(0 to 1, 1 to 2).inOrder()
  }

  @Test
  fun `all non-container inline formats compose instead of replacing inherited attributes`() {
    val html =
      "<strong><b>B</b></strong><em>E</em><cite>C</cite><dfn>D</dfn><i>I</i>" +
        "<u><del>U</del></u><s>S</s><strike>T</strike><tt>M</tt><code>K</code>" +
        "<big>X</big><small>Y</small><sup>P</sup><sub>Q</sub><span style='color:red'><unknown>N</unknown></span>"
    val text = (parseHtml(html).blocks.single() as TextBlock).content.annotate(TextLinkStyles())
    assertThat(text.text).isEqualTo("BECDIUSTMKXYPQN")
    val spans =
      text.indices.map { offset ->
        text.spanStyles.single { offset >= it.start && offset < it.end }.item
      }
    assertThat(spans[0].fontWeight).isEqualTo(FontWeight.Black)
    assertThat(spans.subList(1, 5).map { it.fontStyle })
      .containsExactly(
        androidx.compose.ui.text.font.FontStyle.Italic,
        androidx.compose.ui.text.font.FontStyle.Italic,
        androidx.compose.ui.text.font.FontStyle.Italic,
        androidx.compose.ui.text.font.FontStyle.Italic,
      )
    assertThat(spans[5].textDecoration)
      .isEqualTo(TextDecoration.Underline + TextDecoration.LineThrough)
    assertThat(spans[6].textDecoration).isEqualTo(TextDecoration.LineThrough)
    assertThat(spans[7].textDecoration).isEqualTo(TextDecoration.LineThrough)
    assertThat(spans[8].fontFamily).isEqualTo(androidx.compose.ui.text.font.FontFamily.Monospace)
    assertThat(spans[9].fontFamily).isEqualTo(androidx.compose.ui.text.font.FontFamily.Monospace)
    assertThat(spans[10].fontSize.value).isEqualTo(1.25f)
    assertThat(spans[11].fontSize.value).isEqualTo(.8f)
    assertThat(spans[12].baselineShift)
      .isEqualTo(androidx.compose.ui.text.style.BaselineShift.Superscript)
    assertThat(spans[13].baselineShift)
      .isEqualTo(androidx.compose.ui.text.style.BaselineShift.Subscript)
    assertThat(spans[14].color).isEqualTo(Color.Unspecified)
  }

  @Test
  fun `headings use upstream relative scale and nested bold escalation`() {
    val headings =
      parseHtml("<h1>A<b>B</b></h1><h2>2</h2><h3>3</h3><h4>4</h4><h5>5</h5><h6>6</h6>")
        .blocks
        .filterIsInstance<TextBlock>()
    val rendered = headings.map { it.content.annotate(TextLinkStyles()) }
    assertThat(rendered.map { it.spanStyles.first().item.fontSize.value })
      .containsExactly(1.5f, 1.4f, 1.3f, 1.2f, 1.1f, 1f)
      .inOrder()
    assertThat(rendered.first().spanStyles.map { it.item.fontWeight })
      .containsExactly(FontWeight.Bold, FontWeight.Black)
      .inOrder()
  }

  @Test
  fun `styled anchor emits one transformed URL with nested spans intact`() {
    val inline =
      (parseHtml("<a href='https://example.com'>a<b>b</b>c</a><a href='empty'></a>").blocks.single()
          as TextBlock)
        .content
    var calls = 0
    val styles = TextLinkStyles(SpanStyle(color = Color.Red))
    val text =
      inline.annotate(styles) {
        calls++
        "final:$it"
      }
    assertThat(text.text).isEqualTo("abc")
    val links = text.getLinkAnnotations(0, text.length)
    assertThat(links).hasSize(1)
    assertThat(links.single().start).isEqualTo(0)
    assertThat(links.single().end).isEqualTo(3)
    assertThat((links.single().item as LinkAnnotation.Url).url)
      .isEqualTo("final:https://example.com")
    assertThat((links.single().item as LinkAnnotation.Url).styles).isEqualTo(styles)
    assertThat(calls).isEqualTo(1)
    assertThat(
        text.spanStyles.any {
          it.start == 1 && it.end == 2 && it.item.fontWeight == FontWeight.Bold
        }
      )
      .isTrue()
    assertThat(
        inline
          .annotate(TextLinkStyles(SpanStyle(color = Color.Blue)))
          .getLinkAnnotations(0, 3)
          .single()
          .item
      )
      .isNotEqualTo(links.single().item)
  }
}
