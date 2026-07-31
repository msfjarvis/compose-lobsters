/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

import dev.msfjarvis.claw.database.local.PostComments

internal sealed interface SeenCommentsState {
  data object Loading : SeenCommentsState

  data object NoBaseline : SeenCommentsState

  data class BaselineLoaded(val seenCommentIds: Set<String>) : SeenCommentsState {
    constructor(postComments: PostComments) : this(postComments.commentIds.toSet())
  }

  companion object {
    fun from(seenCommentIds: Set<String>?, hasLoaded: Boolean): SeenCommentsState {
      if (!hasLoaded) return Loading
      return if (seenCommentIds == null) NoBaseline else BaselineLoaded(seenCommentIds)
    }

    fun from(postComments: PostComments?, hasLoaded: Boolean): SeenCommentsState =
      from(postComments?.commentIds?.toSet(), hasLoaded)
  }
}
