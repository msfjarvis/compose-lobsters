/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.database

/** Persistence operations for the comment IDs a user has already seen per post. */
interface CommentsStorage {
  suspend fun getSeenCommentIds(postId: String): Set<String>?

  suspend fun rememberCommentIds(postId: String, commentIds: List<String>)
}
