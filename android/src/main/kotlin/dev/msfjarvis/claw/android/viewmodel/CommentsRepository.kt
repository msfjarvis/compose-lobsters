/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import dev.msfjarvis.claw.core.injection.DatabaseDispatcher
import dev.msfjarvis.claw.database.local.PostComments
import dev.msfjarvis.claw.database.local.PostCommentsQueries
import dev.msfjarvis.claw.model.Comment
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class CommentsRepository
@Inject
constructor(
  private val postCommentsQueries: PostCommentsQueries,
  @DatabaseDispatcher private val dbDispatcher: CoroutineDispatcher,
) {

  suspend fun getSeenComments(postId: String) =
    withContext(dbDispatcher) { postCommentsQueries.getCommentIds(postId).executeAsOneOrNull() }

  suspend fun markSeenComments(postId: String, comments: List<Comment>) {
    withContext(dbDispatcher) {
      postCommentsQueries.rememberComments(PostComments(postId, comments.map { it.shortId }))
    }
  }
}
