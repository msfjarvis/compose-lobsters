/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
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
  val readPosts = readPostsQueries.selectAllPosts().asFlow().mapToList(dbDispatcher)

  suspend fun markRead(postId: String) {
    withContext(dbDispatcher) { readPostsQueries.markRead(postId) }
  }
}
