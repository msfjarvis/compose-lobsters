/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.htmlrenderer

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class QuoteParserTest {
  @Test
  fun `empty nested quotes produce no blocks or extra paragraph gaps`() {
    val blocks =
      parseHtml(
          "<div>A<blockquote><p> </p><blockquote><!--empty--></blockquote></blockquote>B</div>"
        )
        .blocks
    assertThat(blocks.filterIsInstance<TextBlock>().map { it.content.text })
      .containsExactly("A", "B")
      .inOrder()
    assertThat(blockGaps(blocks, 24)).containsExactly(0)
    assertThat(parseHtml("<blockquote><blockquote> </blockquote></blockquote>").blocks).isEmpty()
    val hardBreak = parseHtml("<blockquote><br></blockquote>").blocks.single() as Quote
    assertThat((hardBreak.children.single() as TextBlock).content.text).isEqualTo("\n")
  }

  @Test
  fun `nested paragraphs headings and pre keep source order inherited styles without quote italics`() {
    val blocks =
      parseHtml(
          "<b><blockquote><p>First</p><blockquote><h2>Heading</h2><pre>  code\n</pre></blockquote><p><i>Last</i></p></blockquote></b><p>Outside</p>"
        )
        .blocks
    val outer = blocks.first() as Quote
    assertThat(outer.children).hasSize(3)
    val first = outer.children.first() as TextBlock
    val inner = outer.children[1] as Quote
    val heading = inner.children[0] as TextBlock
    val code = inner.children[1] as TextBlock
    val last = outer.children.last() as TextBlock
    assertThat(
        listOf(first.content.text, heading.content.text, code.content.text, last.content.text)
      )
      .containsExactly("First", "Heading", "  code\n", "Last")
      .inOrder()
    assertThat(first.content.runs.single().style.boldDepth).isEqualTo(1)
    assertThat(first.content.runs.single().style.italic).isFalse()
    assertThat(heading.headingLevel).isEqualTo(2)
    assertThat(heading.content.runs.single().style.boldDepth).isEqualTo(2)
    assertThat(code.content.runs.single().style.monospace).isTrue()
    assertThat(code.content.runs.single().style.italic).isFalse()
    assertThat(last.content.runs.single().style.italic).isTrue()
    assertThat(blockGaps(outer.children, 24)).containsExactly(24, 24)
    assertThat(blockGaps(inner.children, 24)).containsExactly(24)
    assertThat(blockGaps(blocks, 24)).containsExactly(24)
  }
}
