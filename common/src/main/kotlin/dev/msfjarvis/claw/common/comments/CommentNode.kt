/*
 * Copyright © 2022-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

import dev.msfjarvis.claw.model.Comment

internal data class CommentNode(
  val comment: Comment,
  val isPostAuthor: Boolean,
  private var parent: CommentNode? = null,
  val children: MutableList<CommentNode> = mutableListOf(),
  val isUnread: Boolean = false,
  val indentLevel: Int,
  val isExpanded: Boolean = true,
) {

  fun addChild(child: CommentNode) {
    if (comment.shortId == child.comment.parentComment) {
      children.add(child)
      child.parent = this
    } else {
      children
        .lastOrNull()
        ?.addChild(
          CommentNode(
            comment = child.comment,
            isPostAuthor = child.isPostAuthor,
            parent = child.parent,
            isUnread = child.isUnread,
            indentLevel = child.indentLevel + 1,
          )
        )
    }
  }

  /**
   * [CommentNode.equals] and [CommentNode.hashCode] are hand-rolled to drop the
   * [CommentNode.parent] field from the comparison since it's possible for there to be cycles in
   * this comparison check. For our purposes we're fine with foregoing the field.
   */
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as CommentNode

    if (comment != other.comment) return false
    if (children != other.children) return false
    if (isUnread != other.isUnread) return false
    if (indentLevel != other.indentLevel) return false
    if (isExpanded != other.isExpanded) return false

    return true
  }

  override fun hashCode(): Int {
    var result = comment.hashCode()
    result = 31 * result + children.hashCode()
    result = 31 * result + isUnread.hashCode()
    result = 31 * result + indentLevel
    result = 31 * result + isExpanded.hashCode()
    return result
  }
}
