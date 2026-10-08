/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.htmlrenderer

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class HtmlListParserTest {
  @Test
  fun `item prose and nested lists are tight but separate paragraphs retain one gap`() {
    val list =
      parseHtml(
          "<ul><li>before<ul><li><p>nested</p></li></ul>after</li><li><p>A</p><p>B</p></li></ul>"
        )
        .blocks
        .single() as ListBlock
    val items = list.children.filterIsInstance<ListItem>()
    assertThat(blockGaps(items[0].children, 24)).containsExactly(0, 0).inOrder()
    assertThat(blockGaps(items[1].children, 24)).containsExactly(24)
    assertThat(items[0].children.map { if (it is TextBlock) it.content.text else "list" })
      .containsExactly("before", "list", "after")
      .inOrder()
    val nested = items[0].children[1] as ListBlock
    assertThat(((nested.children.single() as ListItem).children.single() as TextBlock).content.text)
      .isEqualTo("nested")
  }

  @Test
  fun `empty wrappers and adjacent three-level list-only items suppress only parent markers`() {
    val list =
      parseHtml(
          "<ul><li> <p></p><!--ignore--><div> <ul><li><ul><li>deep</li></ul></li></ul> </div><p> </p></li><li><ol><li>ordered</li></ol></li><li></li></ul>"
        )
        .blocks
        .single() as ListBlock
    val items = list.children.filterIsInstance<ListItem>()
    assertThat(items).hasSize(2)
    assertThat(items.map { it.markerVisible }).containsExactly(false, false)
    val middle = ((items[0].children.single() as ListBlock).children.single() as ListItem)
    assertThat(middle.markerVisible).isFalse()
    val deep = ((middle.children.single() as ListBlock).children.single() as ListItem)
    assertThat(deep.markerVisible).isTrue()
    assertThat((deep.children.single() as TextBlock).content.text).isEqualTo("deep")
  }

  @Test
  fun `ordered counters consume suppressed and empty direct items with independent nesting`() {
    val list =
      parseHtml(
          "<ol start='9' reversed><li value='42'>A</li><li><ol start='7'><li>B</li><li>C</li></ol></li><li> </li><li>D</li></ol>"
        )
        .blocks
        .single() as ListBlock
    val items = list.children.filterIsInstance<ListItem>()
    assertThat(items.map { it.ordinal }).containsExactly(1, 2, 4).inOrder()
    assertThat(items.map { it.markerVisible }).containsExactly(true, false, true).inOrder()
    val nested = items[1].children.single() as ListBlock
    assertThat(nested.children.filterIsInstance<ListItem>().map { it.ordinal })
      .containsExactly(1, 2)
      .inOrder()
  }

  @Test
  fun `non-list content explicit breaks and quotes keep a parent marker in source order`() {
    val list =
      parseHtml(
          "<ul><li><ul><li>nested</li></ul>after</li><li><br><ul><li>B</li></ul></li><li><blockquote>quote</blockquote></li><li><pre>code</pre></li></ul>"
        )
        .blocks
        .single() as ListBlock
    val items = list.children.filterIsInstance<ListItem>()
    assertThat(items.map { it.markerVisible }).containsExactly(true, true, true, true)
    assertThat(items[0].children[0]).isInstanceOf(ListBlock::class.java)
    assertThat((items[0].children[1] as TextBlock).content.text).isEqualTo("after")
    assertThat((items[1].children[0] as TextBlock).content.text).isEqualTo("\n")
    assertThat(items[2].children.single()).isInstanceOf(Quote::class.java)
  }

  @Test
  fun `non-items under a list retain source positions without promoting descendant items`() {
    val list =
      parseHtml("<ol><li>A</li>unmarked<div><li>descendant</li></div><li>B</li></ol>")
        .blocks
        .single() as ListBlock
    assertThat(list.children).hasSize(4)
    assertThat(list.children.filterIsInstance<ListItem>().map { it.ordinal })
      .containsExactly(1, 2)
      .inOrder()
    assertThat((list.children[1] as TextBlock).content.text).isEqualTo("unmarked")
    val synthetic = list.children[2] as ListBlock
    assertThat(
        ((synthetic.children.single() as ListItem).children.single() as TextBlock).content.text
      )
      .isEqualTo("descendant")
  }

  @Test
  fun `inline wrappers never promote descendant items into the owning list sequence`() {
    val list =
      parseHtml("<ol><li>A</li><custom><li>nested orphan</li></custom><li>B</li></ol>")
        .blocks
        .single() as ListBlock
    assertThat(list.children.filterIsInstance<ListItem>().map { it.ordinal })
      .containsExactly(1, 2)
      .inOrder()
    assertThat(list.children[1]).isInstanceOf(ListBlock::class.java)
  }

  @Test
  fun `migration paragraphs stay with their actual list item`() {
    val html = checkNotNull(javaClass.getResource("/migration.html")).readText()
    val list = parseHtml(html).blocks.filterIsInstance<ListBlock>().single()
    assertThat(list.children.filterIsInstance<ListItem>()).hasSize(4)
    assertThat(list.children.filterIsInstance<ListItem>().map { it.children.size })
      .containsExactly(1, 1, 1, 2)
      .inOrder()
    val last = list.children.filterIsInstance<ListItem>().last()
    assertThat((last.children[0] as TextBlock).content.text)
      .isEqualTo("Why go with a hosting provider and not self-hosting or github.io?")
    assertThat((last.children[1] as ListBlock).children.filterIsInstance<ListItem>()).hasSize(3)
  }

  @Test
  fun `explicit paragraph and hr boundaries beside nested lists remain meaningful`() {
    val list =
      parseHtml(
          "<ul><li><p>A</p><ul><li>B</li></ul><p>C</p></li><li>before<hr><ul><li>D</li></ul>after</li></ul>"
        )
        .blocks
        .single() as ListBlock
    val items = list.children.filterIsInstance<ListItem>()
    assertThat(blockGaps(items[0].children, 24)).containsExactly(24, 24).inOrder()
    assertThat(blockGaps(items[1].children, 24)).containsExactly(0, 24, 0).inOrder()
  }

  @Test
  fun `explicit boundaries break orphan adjacency even when no text block is emitted`() {
    val blocks = parseHtml("<li>A</li><hr><li>B</li><p></p><li>C</li>").blocks
    assertThat(blocks.filterIsInstance<ListBlock>()).hasSize(3)
    assertThat(
        blocks.filterIsInstance<ListBlock>().map { (it.children.single() as ListItem).ordinal }
      )
      .containsExactly(1, 1, 1)
    assertThat(blockGaps(blocks, 24)).containsExactly(0, 24, 24)
  }

  @Test
  fun `adjacent orphan items form independent synthetic lists without absorbing real lists`() {
    val blocks =
      parseHtml(
          "<ul><li>real</li></ul><li>orphan A</li><!--space--> <li>orphan B</li><p>separator</p><li></li><li>orphan C</li>"
        )
        .blocks
    assertThat(blocks).hasSize(4)
    val lists = blocks.filterIsInstance<ListBlock>()
    assertThat(lists.map { it.children.filterIsInstance<ListItem>().map { item -> item.ordinal } })
      .containsExactly(listOf(1), listOf(1, 2), listOf(2))
      .inOrder()
  }

  @Test
  fun `upstream 26 retains depth and text without an orphan parent marker`() {
    val outer =
      parseHtml(
          """
      <ul>
        <li>Our client is facing insurmountable financial difficulties</li>
        <li><ul><li>including job loss and unexpected medical expenses</li></ul></li>
      </ul>
    """
        )
        .blocks
        .single() as ListBlock
    val items = outer.children.filterIsInstance<ListItem>()
    assertThat(items.map { it.markerVisible }).containsExactly(true, false).inOrder()
    val nested = items[1].children.single() as ListBlock
    val child = nested.children.single() as ListItem
    assertThat(child.markerVisible).isTrue()
    assertThat((items[0].children.single() as TextBlock).content.text)
      .isEqualTo("Our client is facing insurmountable financial difficulties")
    assertThat((child.children.single() as TextBlock).content.text)
      .isEqualTo("including job loss and unexpected medical expenses")
  }
}
