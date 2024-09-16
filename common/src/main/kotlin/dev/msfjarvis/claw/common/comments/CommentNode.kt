/*
 * Copyright © 2022-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

import dev.msfjarvis.claw.database.local.PostComments
import dev.msfjarvis.claw.model.Comment

internal class CommentNode(
  val comment: Comment,
  private var parent: CommentNode? = null,
  val children: MutableList<CommentNode> = mutableListOf(),
  val isUnread: Boolean = false,
  val indentLevel: Int,
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

    return true
  }

  override fun hashCode(): Int {
    var result = comment.hashCode()
    result = 31 * result + children.hashCode()
    result = 31 * result + isUnread.hashCode()
    result = 31 * result + indentLevel
    return result
  }
}

internal fun createListNode(
  comments: List<Comment>,
  commentState: PostComments?,
): MutableList<CommentNode> {
  val commentNodes = mutableListOf<CommentNode>()
  val isUnread = { id: String -> commentState?.commentIds?.contains(id) == false }

  for (i in comments.indices) {
    if (comments[i].parentComment == null) {
      commentNodes.add(
        CommentNode(
          comment = comments[i],
          isUnread = isUnread(comments[i].shortId),
          indentLevel = 1,
        )
      )
    } else {
      commentNodes.lastOrNull()?.let { commentNode ->
        commentNode.addChild(
          CommentNode(
            comment = comments[i],
            isUnread = isUnread(comments[i].shortId),
            indentLevel = commentNode.indentLevel + 1,
          )
        )
      }
    }
  }
  return commentNodes
}
