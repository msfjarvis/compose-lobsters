/*
 * Copyright © Harsh Shandilya.
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
  fun `mark post as read returns an ID only once`() {
    val id = UUID.randomUUID().toString()

    assertThat(postQueries.markRead(id).executeAsOne()).isEqualTo(id)
    assertThat(postQueries.markRead(id).executeAsOneOrNull()).isNull()
    assertThat(postQueries.selectAllPosts().executeAsList()).contains(id)

    postQueries.markUnread(id)
    assertThat(postQueries.selectAllPosts().executeAsList()).doesNotContain(id)
  }
}
