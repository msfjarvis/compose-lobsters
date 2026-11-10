/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.database.storage

import dev.msfjarvis.claw.core.coroutines.DatabaseReadDispatcher
import dev.msfjarvis.claw.core.coroutines.DatabaseWriteDispatcher
import dev.msfjarvis.claw.core.database.CommentsStorage
import dev.msfjarvis.claw.database.local.PostComments
import dev.msfjarvis.claw.database.local.PostCommentsQueries
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class SqlDelightCommentsStorage(
  private val queries: PostCommentsQueries,
  @param:DatabaseReadDispatcher private val readDispatcher: CoroutineDispatcher,
  @param:DatabaseWriteDispatcher private val writeDispatcher: CoroutineDispatcher,
) : CommentsStorage {
  override suspend fun getSeenCommentIds(postId: String): Set<String>? =
    withContext(readDispatcher) { queries.getCommentIds(postId).executeAsOneOrNull()?.toSet() }

  override suspend fun rememberCommentIds(postId: String, commentIds: List<String>) {
    withContext(writeDispatcher) {
      queries.rememberComments(PostComments(postId, commentIds))
    }
  }
}
