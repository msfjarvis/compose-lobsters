/*
 * Copyright © 2022-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

import dev.msfjarvis.claw.database.local.PostComments
import dev.msfjarvis.claw.model.Comment

internal data class CommentNode(
  val comment: Comment,
  var parent: CommentNode? = null,
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
}

internal fun createListNode(
  comments: List<Comment>,
  commentState: PostComments,
): MutableList<CommentNode> {
  // Only check unread state if there any comments read in the first place. This somewhat restores
  // the first view behavior from when `commentState` was nullable.
  val hasUnread = commentState.commentIds.isNotEmpty()
  val isUnread =
    if (hasUnread) ({ id: String -> !commentState.commentIds.contains(id) }) else { _ -> false }
  val commentNodes = mutableListOf<CommentNode>()

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
