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

  data class BaselineLoaded(val postComments: PostComments) : SeenCommentsState

  companion object {
    fun from(postComments: PostComments?, hasLoaded: Boolean): SeenCommentsState {
      if (!hasLoaded) return Loading
      return if (postComments == null) NoBaseline else BaselineLoaded(postComments)
    }
  }
}
