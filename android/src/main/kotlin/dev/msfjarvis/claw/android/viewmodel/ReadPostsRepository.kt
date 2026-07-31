/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import dev.msfjarvis.claw.core.coroutines.DatabaseReadDispatcher
import dev.msfjarvis.claw.core.coroutines.DatabaseWriteDispatcher
import dev.msfjarvis.claw.database.local.ReadPostsQueries
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@Inject
@SingleIn(AppScope::class)
class ReadPostsRepository(
  private val readPostsQueries: ReadPostsQueries,
  @param:DatabaseReadDispatcher private val readDispatcher: CoroutineDispatcher,
  @param:DatabaseWriteDispatcher private val writeDispatcher: CoroutineDispatcher,
) {
  private val initializationMutex = Mutex()
  private val _readPosts = MutableStateFlow(emptySet<String>())
  val readPosts: StateFlow<Set<String>> = _readPosts.asStateFlow()
  private var initialized = false

  suspend fun initialize() {
    initializationMutex.withLock { initializeLocked() }
  }

  suspend fun markRead(postId: String) {
    initializationMutex.withLock {
      initializeLocked()
      withContext(writeDispatcher) {
        readPostsQueries.markRead(postId).executeAsOneOrNull()?.let { markedId ->
          _readPosts.update { it + markedId }
        }
      }
    }
  }

  private suspend fun initializeLocked() {
    if (initialized) return
    _readPosts.value =
      withContext(readDispatcher) { readPostsQueries.selectAllPosts().executeAsList().toSet() }
    initialized = true
  }
}
