/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import dev.msfjarvis.claw.core.injection.DatabaseDispatcher
import dev.msfjarvis.claw.database.local.ReadPostsQueries
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ReadPostsRepository
@Inject
constructor(
  private val readPostsQueries: ReadPostsQueries,
  @DatabaseDispatcher private val dbDispatcher: CoroutineDispatcher,
) {

  suspend fun markRead(postId: String) {
    withContext(dbDispatcher) { readPostsQueries.markRead(postId) }
  }

  suspend fun isRead(postId: String): Boolean =
    withContext(dbDispatcher) { readPostsQueries.isRead(postId).executeAsOneOrNull() != null }
}
