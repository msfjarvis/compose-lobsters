/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.database

/** Persistence operations for read-post identifiers. */
interface ReadPostsStorage {
  suspend fun getReadPostIds(): Set<String>

  /** Returns true only when this call inserted a new read marker. */
  suspend fun markRead(postId: String): Boolean
}
