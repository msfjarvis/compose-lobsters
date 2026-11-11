/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

import dev.msfjarvis.claw.core.database.CommentsStorage
import dev.msfjarvis.claw.model.Comment
import dev.zacsweers.metro.Inject

@Inject
internal class CommentsRepository(private val storage: CommentsStorage) {
  suspend fun getSeenComments(postId: String): Set<String>? = storage.getSeenCommentIds(postId)

  suspend fun markSeenComments(postId: String, comments: List<Comment>) {
    storage.rememberCommentIds(postId, comments.map(Comment::shortId))
  }
}
