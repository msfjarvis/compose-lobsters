/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import dev.msfjarvis.claw.model.Comment

data class CommentNode(
  val comment: Comment,
  var parent: CommentNode? = null,
  val children: MutableList<CommentNode> = mutableListOf(),
  var isExpanded: Boolean = true
) {
  fun addChild(child: CommentNode) {
    if (comment.indentLevel + 1 == child.comment.indentLevel) {
      children.add(child)
      child.parent = this
    } else {
      children.last().addChild(child)
    }
  }
}

fun createListNode(comments: List<Comment>): MutableList<CommentNode> {
  val commentNodes = mutableListOf<CommentNode>()

  for (i in comments.indices) {
    if (comments[i].indentLevel == 1) {
      commentNodes.add(CommentNode(comment = comments[i]))
    } else {
      commentNodes.last().addChild(CommentNode(comment = comments[i]))
    }
  }

  return commentNodes
}

@Composable
fun DisplayListNode(
  comments: List<CommentNode>,
  htmlConverter: HTMLConverter,
  updateComments: (CommentNode) -> Unit,
) {
  comments.forEach {
    CommentEntry(commentNode = it, htmlConverter = htmlConverter, updateComments)
    Divider()

    if (it.children.isNotEmpty()) {
      DisplayListNode(comments = it.children, htmlConverter = htmlConverter, updateComments)
    }
  }
}

fun toggleAllExpanded(commentNode: CommentNode): CommentNode {
  commentNode.isExpanded = !commentNode.isExpanded

  if (commentNode.children.isNotEmpty()) {
    commentNode.children.forEach { toggleAllExpanded(it) }
  }
  return commentNode
}
