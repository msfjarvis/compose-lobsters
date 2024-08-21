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
  var indentLevel: Int,
) {

  fun addChild(child: CommentNode) {
    if (comment.shortId == child.comment.parentComment) {
      children.add(child)
      child.parent = this
    } else {
      child.indentLevel += 1
      children.lastOrNull()?.addChild(child)
    }
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
      commentNodes.lastOrNull()?.let {
        it.addChild(
          CommentNode(
            comment = comments[i],
            isUnread = isUnread(comments[i].shortId),
            indentLevel = it.indentLevel + 1,
          )
        )
      }
    }
  }
  return commentNodes
}
