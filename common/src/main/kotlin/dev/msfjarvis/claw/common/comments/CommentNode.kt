/*
 * Copyright © 2022-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Divider
import dev.msfjarvis.claw.database.local.PostComments
import dev.msfjarvis.claw.model.Comment

internal data class CommentNode(
  val comment: Comment,
  var parent: CommentNode? = null,
  val children: MutableList<CommentNode> = mutableListOf(),
  val isUnread: Boolean = false,
  var isExpanded: Boolean = true,
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

internal fun createListNode(
  comments: List<Comment>,
  commentState: PostComments?
): MutableList<CommentNode> {
  val commentNodes = mutableListOf<CommentNode>()
  val isUnread = { id: String -> commentState?.commentIds?.contains(id) == false }

  for (i in comments.indices) {
    if (comments[i].indentLevel == 1) {
      commentNodes.add(CommentNode(comment = comments[i], isUnread = isUnread(comments[i].shortId)))
    } else {
      commentNodes
        .last()
        .addChild(CommentNode(comment = comments[i], isUnread = isUnread(comments[i].shortId)))
    }
  }

  return commentNodes
}

internal fun setExpanded(commentNode: CommentNode, expanded: Boolean): CommentNode {
  commentNode.isExpanded = expanded

  if (commentNode.children.isNotEmpty()) {
    commentNode.children.forEach { setExpanded(it, expanded) }
  }
  return commentNode
}

internal fun findTopMostParent(node: CommentNode): CommentNode {
  val parent = node.parent
  return if (parent != null) {
    findTopMostParent(parent)
  } else {
    node
  }
}

internal fun LazyListScope.nodes(
  nodes: List<CommentNode>,
  toggleExpanded: (CommentNode) -> Unit,
) {
  nodes.forEach { node ->
    node(
      node = node,
      toggleExpanded = toggleExpanded,
    )
  }
}

private fun LazyListScope.node(
  node: CommentNode,
  toggleExpanded: (CommentNode) -> Unit,
) {
  // Skip the node if neither the node nor its parent is expanded
  if (!node.isExpanded && node.parent?.isExpanded == false) {
    return
  }
  item {
    CommentEntry(
      commentNode = node,
      toggleExpanded = toggleExpanded,
    )
    Divider()
  }
  if (node.children.isNotEmpty()) {
    nodes(
      node.children,
      toggleExpanded = toggleExpanded,
    )
  }
}
