/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.database.local

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DailySavedPostNotificationQueriesTest {
  private lateinit var database: dev.msfjarvis.claw.database.LobstersDatabase
  private lateinit var notificationQueries: DailySavedPostNotificationQueries
  private lateinit var postQueries: SavedPostQueries

  @BeforeEach
  fun setup() {
    database = setupDatabase()
    notificationQueries = database.dailySavedPostNotificationQueries
    postQueries = database.savedPostQueries
  }

  @Test
  fun `empty saved posts create no selection`() {
    notificationQueries.createSelectionIfAbsent("2026-09-05", 100L)

    assertThat(notificationQueries.selectionForDate("2026-09-05").executeAsOneOrNull()).isNull()
  }

  @Test
  fun `selection snapshots a saved post and keeps the first selection`() {
    val first = createPost("first")
    val second = createPost("second")
    postQueries.insertOrReplacePost(first)
    postQueries.insertOrReplacePost(second)

    notificationQueries.createSelectionIfAbsent("2026-09-05", 100L)
    val initial = notificationQueries.selectionForDate("2026-09-05").executeAsOne()

    notificationQueries.createSelectionIfAbsent("2026-09-05", 200L)
    val retry = notificationQueries.selectionForDate("2026-09-05").executeAsOne()

    assertThat(listOf(first.shortId, second.shortId)).contains(initial.shortId)
    assertThat(retry).isEqualTo(initial)
  }

  @Test
  fun `separate dates have separate selections`() {
    postQueries.insertOrReplacePost(createPost("only-post"))

    notificationQueries.createSelectionIfAbsent("2026-09-05", 100L)
    notificationQueries.createSelectionIfAbsent("2026-09-06", 200L)

    assertThat(
        notificationQueries.selectionForDate("2026-09-05").executeAsOne().selectedAtEpochMillis
      )
      .isEqualTo(100L)
    assertThat(
        notificationQueries.selectionForDate("2026-09-06").executeAsOne().selectedAtEpochMillis
      )
      .isEqualTo(200L)
  }

  @Test
  fun `markDelivered is idempotent`() {
    postQueries.insertOrReplacePost(createPost("only-post"))
    notificationQueries.createSelectionIfAbsent("2026-09-05", 100L)

    notificationQueries.markDelivered(deliveredAtEpochMillis = 200L, localDate = "2026-09-05")
    notificationQueries.markDelivered(deliveredAtEpochMillis = 300L, localDate = "2026-09-05")

    assertThat(
        notificationQueries.selectionForDate("2026-09-05").executeAsOne().deliveredAtEpochMillis
      )
      .isEqualTo(200L)
  }

  @Test
  fun `saved post changes do not change an existing snapshot`() {
    postQueries.insertOrReplacePost(createPost("post", title = "before"))
    notificationQueries.createSelectionIfAbsent("2026-09-05", 100L)

    postQueries.insertOrReplacePost(createPost("post", title = "after"))
    postQueries.deletePost("post")

    val selection = notificationQueries.selectionForDate("2026-09-05").executeAsOne()
    assertThat(selection.title).isEqualTo("before")
  }

  private fun createPost(id: String, title: String = id) =
    SavedPost(
      shortId = id,
      title = title,
      url = "https://example.test/$id",
      createdAt = "2026-09-05T00:00:00Z",
      commentCount = 0,
      commentsUrl = "https://example.test/$id/comments",
      submitterName = "test-user",
      tags = emptyList(),
      description = "",
      userIsAuthor = false,
    )
}
