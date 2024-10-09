/*
 * Copyright © 2024 Harsh Shandilya.
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

  private val _listItems: MutableStateFlow<List<CommentNode>> = MutableStateFlow(emptyList())
  val listItems: StateFlow<List<CommentNode>> = _listItems.asStateFlow()

  fun createListNode(comments: List<Comment>, commentState: PostComments?) {
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

    _listItems.value = commentNodes
  }

  fun updateListNode(shortId: String, isExpanded: Boolean) {
    fun updateNode(node: CommentNode): CommentNode {
      if (node.comment.shortId == shortId) {
        return node.copy(isExpanded = isExpanded)
      }
      val updatedChildren = node.children.map { updateNode(it) }.toMutableList()
      return node.copy(children = updatedChildren)
    }

    val listNode = _listItems.value.toMutableList()
    for (i in listNode.indices) {
      val node = listNode[i]
      if (node.comment.shortId == shortId || node.children.any { it.comment.shortId == shortId }) {
        listNode[i] = updateNode(node)
        _listItems.value = listNode.toList()
        return
      }
    }
  }
}
