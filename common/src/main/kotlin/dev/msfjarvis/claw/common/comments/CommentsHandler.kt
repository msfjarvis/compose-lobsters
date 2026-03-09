/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

import dev.msfjarvis.claw.database.local.PostComments
import dev.msfjarvis.claw.model.Comment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class CommentsHandler {

  private var rootNodes = emptyList<CommentNode>()
  private val _visibleNodes: MutableStateFlow<List<CommentNode>> = MutableStateFlow(emptyList())
  val listItems: StateFlow<List<CommentNode>> = _visibleNodes.asStateFlow()
  private var collapsedCommentIds = setOf<String>()

  private fun updateVisibleNodes() {
    val flatList = mutableListOf<CommentNode>()
    fun traverse(node: CommentNode) {
      flatList.add(node)
      if (node.isExpanded) {
        node.children.forEach { traverse(it) }
      }
    }
    rootNodes.forEach { traverse(it) }
    _visibleNodes.value = flatList
  }

  fun createListNode(
    comments: List<Comment>,
    commentState: PostComments?,
    isPostAuthor: (Comment) -> Boolean,
  ) {
    val commentNodes = mutableListOf<CommentNode>()
    val isUnread = { id: String -> commentState?.commentIds?.contains(id) != true }

    for (i in comments.indices) {
      val comment = comments[i]
      if (comment.parentComment == null) {
        commentNodes.add(
          CommentNode(
            comment = comment,
            isPostAuthor = isPostAuthor(comment),
            isUnread = isUnread(comment.shortId),
            indentLevel = 1,
            isExpanded = !collapsedCommentIds.contains(comment.shortId),
          )
        )
      } else {
        commentNodes.lastOrNull()?.let { commentNode ->
          commentNode.addChild(
            CommentNode(
              comment = comment,
              isPostAuthor = isPostAuthor(comment),
              isUnread = isUnread(comment.shortId),
              indentLevel = commentNode.indentLevel + 1,
              isExpanded = !collapsedCommentIds.contains(comment.shortId),
            )
          )
        }
      }
    }

    rootNodes = commentNodes
    updateVisibleNodes()
  }

  fun updateListNode(shortId: String, isExpanded: Boolean) {
    fun updateNode(node: CommentNode): CommentNode {
      if (node.comment.shortId == shortId) {
        return node.copy(isExpanded = isExpanded)
      }
      val updatedChildren = node.children.map { updateNode(it) }.toMutableList()
      return node.copy(children = updatedChildren)
    }

    rootNodes = rootNodes.map { updateNode(it) }
    syncCollapsedState()
    updateVisibleNodes()
  }

  private fun syncCollapsedState() {
    fun collectCollapsedIds(nodes: List<CommentNode>): Set<String> {
      return buildSet {
        nodes.forEach { node ->
          if (!node.isExpanded) add(node.comment.shortId)
          addAll(collectCollapsedIds(node.children))
        }
      }
    }
    collapsedCommentIds = collectCollapsedIds(rootNodes)
  }

  fun updateUnreadStatus(commentState: PostComments?) {
    val seenCommentIds = commentState?.commentIds ?: emptySet()

    fun updateNode(node: CommentNode): CommentNode {
      val isUnread = !seenCommentIds.contains(node.comment.shortId)
      val updatedChildren = node.children.map { updateNode(it) }.toMutableList()
      return node.copy(isUnread = isUnread, children = updatedChildren)
    }

    rootNodes = rootNodes.map { updateNode(it) }
    updateVisibleNodes()
  }
}
