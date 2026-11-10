/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class CommentsPageLogicTest {
  @Test
  fun `null repository result maps to no baseline after load completes`() {
    assertThat(SeenCommentsState.from(seenCommentIds = null, hasLoaded = true))
      .isEqualTo(SeenCommentsState.NoBaseline)
  }

  @Test
  fun `unloaded repository result maps to loading state`() {
    assertThat(SeenCommentsState.from(seenCommentIds = null, hasLoaded = false))
      .isEqualTo(SeenCommentsState.Loading)
  }

  @Test
  fun `loaded repository result maps to loaded baseline`() {
    val seenCommentIds = seenCommentIds("c1", "c2")

    assertThat(SeenCommentsState.from(seenCommentIds = seenCommentIds, hasLoaded = true))
      .isEqualTo(SeenCommentsState.BaselineLoaded(seenCommentIds))
  }

  @Test
  fun `loading state does not persist visit decision`() {
    assertThat(
        buildCommentsVisitDecision(
          currentCommentIds = emptyList(),
          seenCommentsState = SeenCommentsState.Loading,
        )
      )
      .isEqualTo(CommentsVisitDecision(unreadCount = 0, shouldPersist = false))
  }

  @Test
  fun `first visit with zero comments persists after load completes`() {
    assertThat(
        buildCommentsVisitDecision(
          currentCommentIds = emptyList(),
          seenCommentsState = SeenCommentsState.NoBaseline,
        )
      )
      .isEqualTo(CommentsVisitDecision(unreadCount = 0, shouldPersist = true))
  }

  @Test
  fun `unchanged baseline produces no unread toast`() {
    assertThat(
        buildCommentsVisitDecision(
          currentCommentIds = listOf("c1", "c2"),
          seenCommentsState = SeenCommentsState.BaselineLoaded(seenCommentIds("c1", "c2")),
        )
      )
      .isEqualTo(CommentsVisitDecision(unreadCount = 0, shouldPersist = true))
  }

  @Test
  fun `first visit with existing comments returns unreadCount=0 and shouldPersist=true`() {
    assertThat(
        buildCommentsVisitDecision(
          currentCommentIds = listOf("c1", "c2"),
          seenCommentsState = SeenCommentsState.NoBaseline,
        )
      )
      .isEqualTo(CommentsVisitDecision(unreadCount = 0, shouldPersist = true))
  }

  @Test
  fun `empty loaded baseline with comments returns all comments unread for visit decision`() {
    assertThat(
        buildCommentsVisitDecision(
          currentCommentIds = listOf("c1", "c2", "c3"),
          seenCommentsState = SeenCommentsState.BaselineLoaded(emptySet()),
        )
      )
      .isEqualTo(CommentsVisitDecision(unreadCount = 3, shouldPersist = true))
  }

  @Test
  fun `later visit with new ids returns only the new id count for visit decision`() {
    assertThat(
        buildCommentsVisitDecision(
          currentCommentIds = listOf("c1", "c2", "c3", "c4"),
          seenCommentsState = SeenCommentsState.BaselineLoaded(seenCommentIds("c1", "c2")),
        )
      )
      .isEqualTo(CommentsVisitDecision(unreadCount = 2, shouldPersist = true))
  }

  @Test
  fun `size preserving id delta returns exactly one unread for visit decision`() {
    assertThat(
        buildCommentsVisitDecision(
          currentCommentIds = listOf("c2", "c3"),
          seenCommentsState = SeenCommentsState.BaselineLoaded(seenCommentIds("c1", "c2")),
        )
      )
      .isEqualTo(CommentsVisitDecision(unreadCount = 1, shouldPersist = true))
  }

  @Test
  fun `shouldRenderCommentsContent loading is false`() {
    assertThat(SeenCommentsState.Loading != SeenCommentsState.Loading).isFalse()
  }

  @Test
  fun `shouldRenderCommentsContent no baseline is true`() {
    assertThat(SeenCommentsState.NoBaseline != SeenCommentsState.Loading).isTrue()
  }

  @Test
  fun `shouldRenderCommentsContent baseline loaded is true`() {
    assertThat(SeenCommentsState.BaselineLoaded(emptySet()) != SeenCommentsState.Loading).isTrue()
  }

  private fun seenCommentIds(vararg ids: String): Set<String> = ids.toSet()
}
