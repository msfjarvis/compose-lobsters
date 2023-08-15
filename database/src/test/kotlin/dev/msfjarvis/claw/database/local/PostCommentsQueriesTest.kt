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

class PostCommentsQueriesTest {
  private lateinit var postQueries: PostCommentsQueries

  @BeforeEach
  fun setup() {
    postQueries = setupDatabase().postCommentsQueries
  }

  @Test
  fun `get non-existent post`() {
    val ids = postQueries.getCommentIds(UUID.randomUUID().toString()).executeAsOneOrNull()
    assertThat(ids).isNull()
  }

  @Test
  fun `put and get post comments`() {
    val postId = UUID.randomUUID().toString()
    val comments = PostComments(postId, List(10) { UUID.randomUUID().toString() })
    postQueries.rememberComments(comments)

    val ids = postQueries.getCommentIds(postId).executeAsOne().commentIds
    assertThat(ids).hasSize(10)
  }
}
