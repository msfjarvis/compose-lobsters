/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

import com.google.common.truth.Truth.assertThat
import dev.msfjarvis.claw.database.local.PostComments
import dev.msfjarvis.claw.model.Comment
import java.time.Instant
import org.junit.jupiter.api.Test

class CommentsHandlerTest {

  @Test
  fun `create list node marks first visit comments as read`() {
    val handler = CommentsHandler()

    handler.createListNode(
      comments(),
      seenCommentsState = SeenCommentsState.NoBaseline,
      isPostAuthor = { false },
    )

    assertThat(handler.listItems.value.unreadStates())
      .containsExactly("c1" to false, "c2" to false, "c3" to false)
      .inOrder()
  }

  @Test
  fun `create list node marks all current comments unread for empty baseline`() {
    val handler = CommentsHandler()

    handler.createListNode(
      comments(),
      seenCommentsState =
        SeenCommentsState.BaselineLoaded(PostComments(postId = "post", commentIds = emptyList())),
      isPostAuthor = { false },
    )

    assertThat(handler.listItems.value.unreadStates())
      .containsExactly("c1" to true, "c2" to true, "c3" to true)
      .inOrder()
  }

  @Test
  fun `create list node keeps all comments read when baseline exactly matches current ids`() {
    val handler = CommentsHandler()

    handler.createListNode(
      comments(),
      seenCommentsState =
        SeenCommentsState.BaselineLoaded(
          PostComments(postId = "post", commentIds = listOf("c1", "c2", "c3"))
        ),
      isPostAuthor = { false },
    )

    assertThat(handler.listItems.value.unreadStates())
      .containsExactly("c1" to false, "c2" to false, "c3" to false)
      .inOrder()
  }

  @Test
  fun `update unread status marks only new ids unread`() {
    val handler = CommentsHandler()

    handler.createListNode(
      comments(),
      seenCommentsState = SeenCommentsState.NoBaseline,
      isPostAuthor = { false },
    )

    handler.updateUnreadStatus(
      SeenCommentsState.BaselineLoaded(
        PostComments(postId = "post", commentIds = listOf("c1", "c2"))
      )
    )

    assertThat(handler.listItems.value.unreadStates())
      .containsExactly("c1" to false, "c2" to false, "c3" to true)
      .inOrder()
  }

  @Test
  fun `update unread status keeps nodes read when no baseline exists`() {
    val handler = CommentsHandler()

    handler.createListNode(
      comments(),
      seenCommentsState =
        SeenCommentsState.BaselineLoaded(PostComments(postId = "post", commentIds = emptyList())),
      isPostAuthor = { false },
    )

    handler.updateUnreadStatus(seenCommentsState = SeenCommentsState.NoBaseline)

    assertThat(handler.listItems.value.unreadStates())
      .containsExactly("c1" to false, "c2" to false, "c3" to false)
      .inOrder()
  }

  @Test
  fun `size preserving id delta marks only c3 unread`() {
    val handler = CommentsHandler()

    handler.createListNode(
      comments(shortIds = listOf("c2", "c3")),
      seenCommentsState = SeenCommentsState.NoBaseline,
      isPostAuthor = { false },
    )

    handler.updateUnreadStatus(
      SeenCommentsState.BaselineLoaded(
        PostComments(postId = "post", commentIds = listOf("c1", "c2"))
      )
    )

    assertThat(handler.listItems.value.unreadStates())
      .containsExactly("c2" to false, "c3" to true)
      .inOrder()
  }

  @Test
  fun `update unread status ignores loading state`() {
    val handler = CommentsHandler()

    handler.createListNode(
      comments(),
      seenCommentsState =
        SeenCommentsState.BaselineLoaded(PostComments(postId = "post", commentIds = emptyList())),
      isPostAuthor = { false },
    )

    handler.updateUnreadStatus(seenCommentsState = SeenCommentsState.Loading)

    assertThat(handler.listItems.value.unreadStates())
      .containsExactly("c1" to true, "c2" to true, "c3" to true)
      .inOrder()
  }

  private fun List<CommentNode>.unreadStates(): List<Pair<String, Boolean>> {
    return map { it.comment.shortId to it.isUnread }
  }

  private fun comments(shortIds: List<String> = listOf("c1", "c2", "c3")): List<Comment> {
    return shortIds.mapIndexed { index, shortId ->
      comment(shortId = shortId, parentComment = if (index == 1) shortIds.first() else null)
    }
  }

  private fun comment(shortId: String, parentComment: String? = null): Comment {
    return Comment(
      shortId = shortId,
      comment = "Comment $shortId",
      url = "https://lobste.rs/s/$shortId",
      score = 1,
      createdAt = Instant.EPOCH,
      lastEditedAt = Instant.EPOCH,
      parentComment = parentComment,
      user = "user-$shortId",
    )
  }
}
