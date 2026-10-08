/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.htmlrenderer

import kotlinx.collections.immutable.ImmutableList

internal data class Document(val blocks: ImmutableList<Block>)

internal sealed interface Block {
  val before: Boolean
  val after: Boolean
}

internal data class TextBlock(
  val content: InlineContent,
  val kind: TextKind = TextKind.Plain,
  val headingLevel: Int = 0,
  override val before: Boolean = kind != TextKind.Plain,
  override val after: Boolean = kind != TextKind.Plain,
) : Block

internal enum class TextKind {
  Plain,
  Paragraph,
  Heading,
  Preformatted,
  DefinitionTerm,
  DefinitionDescription,
}

internal data class InlineContent(val runs: ImmutableList<InlineRun>) {
  val text: String
    get() = runs.joinToString("") { it.text }
}

internal data class InlineRun(val text: String, val style: InlineStyle = InlineStyle())

internal data class Anchor(val id: Int, val destination: String)

internal data class InlineStyle(
  val boldDepth: Int = 0,
  val italic: Boolean = false,
  val underline: Boolean = false,
  val strike: Boolean = false,
  val monospace: Boolean = false,
  val scale: Float = 1f,
  val baseline: Float = 0f,
  val anchor: Anchor? = null,
)

// These containers deliberately retain structure for the independent list/quote slices.
internal data class Quote(
  val children: ImmutableList<Block>,
  override val before: Boolean = true,
  override val after: Boolean = true,
) : Block

internal data class ListBlock(
  val ordered: Boolean,
  val children: ImmutableList<Block>,
  override val before: Boolean = true,
  override val after: Boolean = true,
) : Block

internal data class ListItem(
  val children: ImmutableList<Block>,
  val ordinal: Int,
  val markerVisible: Boolean = children.any { it !is ListBlock && it !is HrSeparator },
  override val before: Boolean = false,
  override val after: Boolean = false,
) : Block

internal data class DefinitionDescription(
  val children: ImmutableList<Block>,
  override val before: Boolean = true,
  override val after: Boolean = true,
) : Block

internal data object HrSeparator : Block {
  override val before: Boolean = false
  override val after: Boolean = true
}
