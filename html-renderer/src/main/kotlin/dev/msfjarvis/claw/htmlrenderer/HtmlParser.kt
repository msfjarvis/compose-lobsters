/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.htmlrenderer

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

internal fun parseHtml(html: String): Document = HtmlNormalizer().parse(html)

private val skippedTags = setOf("script", "head", "table", "form", "fieldset")
private val headingTags = setOf("h1", "h2", "h3", "h4", "h5", "h6")
private val genericTags =
  setOf(
    "div",
    "header",
    "footer",
    "main",
    "nav",
    "aside",
    "section",
    "article",
    "address",
    "figure",
    "figcaption",
    "video",
    "audio",
    "dl",
  )

private class HtmlNormalizer {
  private var anchorId = 0

  fun parse(html: String): Document =
    Document(blocks(Ksoup.parse(html).body().childNodes(), InlineStyle()))

  private fun blocks(
    nodes: List<Node>,
    inherited: InlineStyle,
    kind: TextKind = TextKind.Plain,
    heading: Int = 0,
    pre: Boolean = false,
    listOwner: Boolean = false,
    itemOwner: Boolean = false,
  ): ImmutableList<Block> {
    val result = mutableListOf<Block>()
    var buffer = InlineBuffer()
    var pendingGap = false
    var ordinal = 0
    var orphanOrdinal = 0
    var orphanListIndex: Int? = null
    fun emit(block: Block) {
      orphanOrdinal = 0
      orphanListIndex = null
      result += if (pendingGap) block.withEdges(before = true) else block
      pendingGap = false
    }
    fun flush() {
      val content = buffer.finish()
      if (content.runs.isNotEmpty())
        emit(TextBlock(content, kind, heading, before = false, after = false))
      buffer = InlineBuffer()
    }
    fun visit(node: Node, style: InlineStyle, direct: Boolean = true) {
      when (node) {
        is TextNode -> buffer.append(node.getWholeText(), style, pre)
        is Element -> {
          val tag = node.tagName()
          if (tag != "li") {
            orphanOrdinal = 0
            orphanListIndex = null
          }
          if (tag in skippedTags) return
          val nested = style.nestedStyleFor(node, tag)
          when (tag) {
            "br" -> buffer.hardBreak(nested)
            "hr" -> {
              flush()
              if (result.lastOrNull() !is HrSeparator) result += HrSeparator
              pendingGap = false
            }
            "blockquote" -> {
              flush()
              val children = blocks(node.childNodes(), nested, pre = pre)
              if (children.isNotEmpty()) emit(Quote(children))
            }
            "ul",
            "ol" -> {
              flush()
              val children = blocks(node.childNodes(), nested, pre = pre, listOwner = true)
              if (children.isNotEmpty())
                emit(ListBlock(tag == "ol", children, before = !itemOwner, after = !itemOwner))
            }
            "li" -> {
              flush()
              val owned = listOwner && direct
              if (owned) ordinal++ else orphanOrdinal++
              // List boundaries alone do not separate item prose; explicit paragraphs still do.
              val children = blocks(node.childNodes(), nested, pre = pre, itemOwner = true)
              if (children.isNotEmpty()) {
                val item = ListItem(children, if (owned) ordinal else orphanOrdinal)
                if (owned) emit(item)
                else {
                  val previousIndex = orphanListIndex
                  if (previousIndex != null) {
                    val previous = result[previousIndex] as ListBlock
                    result[previousIndex] =
                      previous.copy(children = (previous.children + item).toImmutableList())
                  } else {
                    orphanListIndex = result.size
                    result += ListBlock(false, persistentListOf(item), before = true)
                    pendingGap = false
                  }
                }
              }
            }
            in genericTags,
            "p",
            "pre",
            "dt",
            "dd",
            in headingTags -> {
              flush()
              val blockKind =
                when (tag) {
                  "p" -> TextKind.Paragraph
                  "pre" -> TextKind.Preformatted
                  "dt" -> TextKind.DefinitionTerm
                  "dd" -> TextKind.DefinitionDescription
                  "h1",
                  "h2",
                  "h3",
                  "h4",
                  "h5",
                  "h6" -> TextKind.Heading

                  else -> kind
                }
              val level = if (tag in headingTags) tag.last().digitToInt() else heading
              val blockStyle =
                if (tag in headingTags)
                  nested.copy(
                    boldDepth = nested.boldDepth + 1,
                    scale = nested.scale * listOf(1.5f, 1.4f, 1.3f, 1.2f, 1.1f, 1f)[level - 1],
                  )
                else nested
              val children =
                blocks(
                  node.childNodes(),
                  blockStyle,
                  blockKind,
                  level,
                  pre || tag == "pre",
                  itemOwner = itemOwner,
                )
              if (tag == "dd" && children.isNotEmpty()) {
                emit(DefinitionDescription(children))
              } else {
                children.forEachIndexed { index, child ->
                  emit(
                    if (tag in genericTags) child
                    else
                      child.withEdges(
                        before = child.before || index == 0,
                        after = child.after || index == children.lastIndex,
                      )
                  )
                }
              }
            }
            else -> node.childNodes().forEach { visit(it, nested, direct = false) }
          }
        }
      }
    }
    nodes.forEach { visit(it, inherited) }
    flush()
    return result.toImmutableList()
  }

  private fun InlineStyle.nestedStyleFor(node: Node, tag: String): InlineStyle {
    return when (tag) {
      "b",
      "strong" -> copy(boldDepth = boldDepth + 1)
      "em",
      "cite",
      "dfn",
      "i" -> copy(italic = true)
      "u" -> copy(underline = true)
      "del",
      "s",
      "strike" -> copy(strike = true)
      "tt",
      "code",
      "pre" -> copy(monospace = true)
      "big" -> copy(scale = scale * 1.25f)
      "small" -> copy(scale = scale * .8f)
      "sup" -> copy(baseline = .5f)
      "sub" -> copy(baseline = -.5f)
      "a" ->
        if (node.hasAttr("href")) copy(anchor = Anchor(anchorId++, node.attr("href"))) else this
      else -> this
    }
  }
}

internal fun Block.withEdges(before: Boolean = this.before, after: Boolean = this.after): Block =
  when (this) {
    is TextBlock -> copy(before = before, after = after)
    is Quote -> copy(before = before, after = after)
    is ListBlock -> copy(before = before, after = after)
    is ListItem -> copy(before = before, after = after)
    is DefinitionDescription -> copy(before = before, after = after)
    HrSeparator -> this
  }

private class InlineBuffer {
  private val runs = mutableListOf<InlineRun>()
  private val currentText = StringBuilder()
  private var currentStyle: InlineStyle? = null
  private var pending: InlineStyle? = null
  private var lastChar: Char? = null

  fun append(text: String, style: InlineStyle, pre: Boolean = false) {
    if (pre) {
      val normalized = text.replace("\r\n", "\n").replace('\r', '\n')
      if (normalized.isNotEmpty()) {
        selectStyle(style)
        currentText.append(normalized)
        lastChar = normalized.last()
      }
      return
    }
    for (char in text) {
      if (char in " \t\n\r\u000c") pending = style
      else {
        val whitespaceStyle = pending
        if (whitespaceStyle != null && lastChar != null && lastChar != '\n')
          add(' ', whitespaceStyle)
        pending = null
        add(char, style)
      }
    }
  }

  fun hardBreak(style: InlineStyle) {
    pending = null
    add('\n', style)
  }

  private fun add(char: Char, style: InlineStyle) {
    selectStyle(style)
    currentText.append(char)
    lastChar = char
  }

  private fun selectStyle(style: InlineStyle) {
    if (currentStyle != style) {
      flushRun()
      currentStyle = style
    }
  }

  private fun flushRun() {
    if (currentText.isNotEmpty())
      runs +=
        InlineRun(
          currentText.toString(),
          checkNotNull(currentStyle) { "Every buffered character has an inherited inline style" },
        )
    currentText.setLength(0)
  }

  fun finish(): InlineContent {
    flushRun()
    return InlineContent(runs.toImmutableList())
  }
}
