/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.posts.impl

import dev.msfjarvis.claw.core.database.ReadPostsStorage
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Inject
@SingleIn(AppScope::class)
class ReadPostsRepository(private val storage: ReadPostsStorage) {
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
      if (storage.markRead(postId)) _readPosts.update { it + postId }
    }
  }

  private suspend fun initializeLocked() {
    if (initialized) return
    _readPosts.value = storage.getReadPostIds()
    initialized = true
  }
}
