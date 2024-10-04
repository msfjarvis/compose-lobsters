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
    val listNode = _listItems.value.toMutableList()
    val index = listNode.indexOfFirst { it.comment.shortId == shortId }

    if (index != -1) {
      val commentNode = listNode[index].copy(isExpanded = isExpanded)
      listNode[index] = commentNode

      _listItems.value = listNode.toList()
    }
  }
}
