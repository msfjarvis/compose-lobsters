/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.database.local

import com.google.common.truth.Truth.assertThat
import java.util.UUID
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ReadPostsQueriesTest {
  private lateinit var postQueries: ReadPostsQueries

  @BeforeEach
  fun setup() {
    postQueries = setupDatabase().readPostsQueries
  }

  @Test
  fun markPostAsRead() {
    val id = UUID.randomUUID().toString()
    postQueries.markRead(id)
    assertThat(postQueries.isRead(id).executeAsOne()).isNotNull()
    postQueries.markUnread(id)
    assertThat(postQueries.isRead(id).executeAsOneOrNull()).isNull()
  }
}
