/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Divider
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

fun toggleAllExpanded(commentNode: CommentNode): CommentNode {
  commentNode.isExpanded = !commentNode.isExpanded

  if (commentNode.children.isNotEmpty()) {
    commentNode.children.forEach { toggleAllExpanded(it) }
  }
  return commentNode
}

fun findTopMostParent(node: CommentNode): CommentNode {
  val parent = node.parent
  return if (parent != null) {
    findTopMostParent(parent)
  } else {
    node
  }
}

fun LazyListScope.nodes(
  nodes: List<CommentNode>,
  htmlConverter: HTMLConverter,
  toggleExpanded: (CommentNode) -> Unit,
) {
  nodes.forEach { node ->
    node(
      node = node,
      htmlConverter = htmlConverter,
      toggleExpanded = toggleExpanded,
    )
  }
}

fun LazyListScope.node(
  node: CommentNode,
  htmlConverter: HTMLConverter,
  toggleExpanded: (CommentNode) -> Unit,
) {
  item {
    CommentEntry(
      commentNode = node,
      htmlConverter = htmlConverter,
      toggleExpanded = toggleExpanded,
    )
    Divider()
  }
  if (node.children.isNotEmpty()) {
    nodes(
      node.children,
      htmlConverter = htmlConverter,
      toggleExpanded = toggleExpanded,
    )
  }
}
