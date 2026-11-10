/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.database.storage

import dev.msfjarvis.claw.core.coroutines.DatabaseReadDispatcher
import dev.msfjarvis.claw.core.coroutines.DatabaseWriteDispatcher
import dev.msfjarvis.claw.core.database.ReadPostsStorage
import dev.msfjarvis.claw.database.local.ReadPostsQueries
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class SqlDelightReadPostsStorage(
  private val queries: ReadPostsQueries,
  @param:DatabaseReadDispatcher private val readDispatcher: CoroutineDispatcher,
  @param:DatabaseWriteDispatcher private val writeDispatcher: CoroutineDispatcher,
) : ReadPostsStorage {
  override suspend fun getReadPostIds(): Set<String> =
    withContext(readDispatcher) { queries.selectAllPosts().executeAsList().toSet() }

  override suspend fun markRead(postId: String): Boolean =
    withContext(writeDispatcher) { queries.markRead(postId).executeAsOneOrNull() != null }
}
