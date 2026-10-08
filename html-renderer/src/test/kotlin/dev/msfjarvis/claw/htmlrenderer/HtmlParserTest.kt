/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.htmlrenderer

import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLinkStyles
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class HtmlParserTest {
  @Test
  fun `only HTML ASCII whitespace collapses while NBSP vertical tab and Unicode survive`() {
    val input = "\t A\r\n<b>\u000c B </b>\t<a href='x'> C </a>D\u00a0\u00a0🙂日本語\u000b E "
    assertThat((parseHtml(input).blocks.single() as TextBlock).content.text)
      .isEqualTo("A B C D\u00a0\u00a0🙂日本語\u000b E")
    val prose = "🙂日本語 ".repeat(10000).trimEnd()
    assertThat((parseHtml(prose).blocks.single() as TextBlock).content.text).isEqualTo(prose)
  }

  @Test
  fun `pre preserves decoded whitespace trailing newline and nested markup`() {
    val pre =
      parseHtml("<pre>  A\r\n <b>B</b>\r<a href='x'><i>C</i></a>\n&amp;lt;\n</pre>").blocks.single()
        as TextBlock
    assertThat(pre.content.text).isEqualTo("  A\n B\nC\n&lt;\n")
    assertThat(pre.content.runs.all { it.style.monospace }).isTrue()
    assertThat(pre.content.runs.single { it.text == "B" }.style.boldDepth).isEqualTo(1)
    assertThat(pre.content.runs.single { it.text == "C" }.style.italic).isTrue()
    assertThat(pre.content.runs.single { it.text == "C" }.style.anchor?.destination).isEqualTo("x")
  }

  @Test
  fun `generic blocks inside headings preserve formatting with single boundaries`() {
    val blocks = parseHtml("<h2>A<div>B</div>C</h2><p>D</p>").blocks.filterIsInstance<TextBlock>()
    assertThat(blocks.map { it.content.text }).containsExactly("A", "B", "C", "D").inOrder()
    assertThat(blocks.take(3).map { it.headingLevel }).containsExactly(2, 2, 2)
    assertThat(blocks.take(3).flatMap { it.content.runs }.map { it.style.scale })
      .containsExactly(1.4f, 1.4f, 1.4f)
    assertThat(blockGaps(blocks, 24)).containsExactly(0, 0, 24).inOrder()
  }

  @Test
  fun `unsupported subtrees disappear but unknown textual children survive malformed HTML`() {
    val blocks =
      parseHtml(
          "<p>one<b>two<p>three</b><table><tr><td>hidden</td></tr></table><form>hidden</form><fieldset>hidden</fieldset><script>hidden</script><!--hidden--><custom>four</custom><img src='https://example.com/no-fetch'>"
        )
        .blocks
    assertThat(blocks.filterIsInstance<TextBlock>().joinToString("|") { it.content.text })
      .isEqualTo("onetwo|three|four")
  }

  @Test
  fun `migration fixture retains prose nested containers markup and links in order`() {
    // Exercise equivalent encoded punctuation without changing the complete migration resource.
    val html =
      checkNotNull(javaClass.getResource("/migration.html"))
        .readText()
        .replace("don't", "don&apos;t")
        .replace("\"websocket\"", "&quot;websocket&quot;")
    val document = parseHtml(html)
    fun texts(blocks: List<Block>): List<TextBlock> = blocks.flatMap {
      when (it) {
        is TextBlock -> listOf(it)
        is Quote -> texts(it.children)
        is ListBlock -> texts(it.children)
        is ListItem -> texts(it.children)
        is DefinitionDescription -> texts(it.children)
        HrSeparator -> emptyList()
      }
    }
    val text = texts(document.blocks)
    assertThat(text.map { it.content.text.take(20) })
      .containsExactly(
        "Most lobsters I enco",
        "If you don't have a ",
        "I only set up my own",
        "I set up hosting wit",
        "Beyond uploading fil",
        "To preview your site",
        "Why go with a hostin",
        "I don't trust GitHub",
        "I don't want to beco",
        "Owning your own doma",
        "To be clear, I am no",
      )
      .inOrder()
    assertThat(document.blocks.map { it::class.simpleName })
      .containsExactly("TextBlock", "HrSeparator", "Quote", "TextBlock", "ListBlock", "TextBlock")
      .inOrder()
    assertThat(text.map { it.kind })
      .containsExactly(
        TextKind.Paragraph,
        TextKind.Paragraph,
        TextKind.Paragraph,
        TextKind.Paragraph,
        TextKind.Paragraph,
        TextKind.Paragraph,
        TextKind.Paragraph,
        TextKind.Plain,
        TextKind.Plain,
        TextKind.Plain,
        TextKind.Paragraph,
      )
      .inOrder()
    val list = document.blocks.filterIsInstance<ListBlock>().single()
    assertThat(list.ordered).isFalse()
    val items = list.children.filterIsInstance<ListItem>()
    assertThat(items.map { it.ordinal }).containsExactly(1, 2, 3, 4).inOrder()
    assertThat(items.map { it.children.size }).containsExactly(1, 1, 1, 2).inOrder()
    assertThat(items.all { it.markerVisible }).isTrue()
    val nested = items[3].children[1] as ListBlock
    assertThat(nested.ordered).isTrue()
    assertThat(nested.children.filterIsInstance<ListItem>().map { it.ordinal })
      .containsExactly(1, 2, 3)
      .inOrder()
    assertThat(text[2].content.text).endsWith("Here's my advice:\n(assuming you're new to this 🙂)")
    // Only the source br introduces a newline: no serialized sibling separators survive.
    assertThat(text.map { it.content.text.count { char -> char == '\n' } })
      .containsExactly(0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0)
      .inOrder()
    assertThat(text[5].content.text).contains("creates a \"websocket\"")
    assertThat(text[9].content.runs.single { it.text == "somewhere" }.style.italic).isTrue()
    assertThat(text[4].content.runs.filter { it.style.monospace }.map { it.text })
      .containsExactly(
        ".htaccess",
        ".htaccess",
        "mod rewrite",
        "mod deflate",
        "mod rewrite",
        "/xyz.html",
        "/xyz",
      )
      .inOrder()
    val annotated = text.map { it.content.annotate(TextLinkStyles()) { url -> "final:$url" } }
    val links = annotated.flatMap { value ->
      value.getLinkAnnotations(0, value.length).map { range ->
        (range.item as LinkAnnotation.Url).url to value.text.substring(range.start, range.end)
      }
    }
    assertThat(links)
      .containsExactly(
        "final:https://www.tigertech.net" to "tigertech.net",
        "final:https://www.tigertech.net/customerlist/700" to "cppreference.com",
        "final:https://support.tigertech.net/htaccess-topic" to ".htaccess file",
        "final:https://support.tigertech.net/domain-transfer-away" to
          "transferring your domain away from them",
      )
      .inOrder()
    assertThat(
        text.flatMap { it.content.runs }.mapNotNull { it.style.anchor?.destination }.distinct()
      )
      .containsExactly(
        "https://www.tigertech.net",
        "https://www.tigertech.net/customerlist/700",
        "https://support.tigertech.net/htaccess-topic",
        "https://support.tigertech.net/domain-transfer-away",
      )
      .inOrder()
    assertThat(
        text
          .flatMap { it.content.runs }
          .any { it.text == "away from them" && it.style.boldDepth == 1 }
      )
      .isTrue()
  }

  @Test
  fun `generic boundaries and hr use max combined gaps without empty texts or outer spacers`() {
    val blocks =
      parseHtml("<hr><div>A<section>B</section>C</div><hr><hr><p>D</p><p>E</p><hr>").blocks
    assertThat(blocks.filterIsInstance<TextBlock>().map { it.content.text })
      .containsExactly("A", "B", "C", "D", "E")
      .inOrder()
    assertThat(blockGaps(blocks, 24)).containsExactly(0, 0, 0, 0, 24, 24, 0).inOrder()
    assertThat(
        parseHtml("  <!-- ignored --><script>x</script><p> </p><blockquote> </blockquote>").blocks
      )
      .isEmpty()
    assertThat((parseHtml("<br><br>").blocks.single() as TextBlock).content.text).isEqualTo("\n\n")
  }

  @Test
  fun `blocks retain source order inherited styles and preformatted whitespace`() {
    val blocks =
      parseHtml(
          "<b>A<div>B<p>C</p>D</div>E</b><pre>  x\n <i> y</i>\n</pre><dl><dt>Term</dt><dd>Description</dd></dl>"
        )
        .blocks
    val textBlocks = blocks.flatMap { block ->
      when (block) {
        is DefinitionDescription -> block.children.filterIsInstance<TextBlock>()
        is TextBlock -> listOf(block)
        else -> emptyList()
      }
    }
    assertThat(textBlocks.map { it.content.text })
      .containsExactly("A", "B", "C", "D", "E", "  x\n  y\n", "Term", "Description")
      .inOrder()
    assertThat(textBlocks.take(5).flatMap { it.content.runs }.map { it.style.boldDepth })
      .containsExactly(1, 1, 1, 1, 1)
    assertThat(textBlocks.map { it.kind })
      .containsExactly(
        TextKind.Plain,
        TextKind.Plain,
        TextKind.Paragraph,
        TextKind.Plain,
        TextKind.Plain,
        TextKind.Preformatted,
        TextKind.DefinitionTerm,
        TextKind.DefinitionDescription,
      )
      .inOrder()
  }

  @Test
  fun `definition descriptions retain nested blocks as one indented container`() {
    val description =
      parseHtml(
          "<dl><dd><p>Description</p><ul><li>Nested</li></ul><blockquote><p>Quoted</p></blockquote></dd></dl>"
        )
        .blocks
        .single() as DefinitionDescription
    assertThat(description.children.map { it::class.simpleName })
      .containsExactly("TextBlock", "ListBlock", "Quote")
      .inOrder()
    assertThat((description.children.first() as TextBlock).content.text).isEqualTo("Description")
  }

  @Test
  fun `hr in a wrapper survives flattening as one paragraph separator request`() {
    val blocks = parseHtml("A<div><hr></div>B").blocks
    assertThat(blocks).hasSize(3)
    assertThat(blocks[1]).isEqualTo(HrSeparator)
    assertThat(blockGaps(blocks, 24)).containsExactly(0, 24).inOrder()
    assertThat(blockGaps(parseHtml("A<hr><hr>B").blocks, 24)).containsExactly(0, 24).inOrder()
  }

  @Test
  fun `whitespace entities and hard breaks survive normalization`() {
    val blocks = parseHtml("  A <b> B </b> C&nbsp;&amp;lt;<br><br> D ").blocks
    assertThat(blocks.filterIsInstance<TextBlock>().map { it.content.text })
      .containsExactly("A B C\u00a0&lt;\n\nD")
  }
}
