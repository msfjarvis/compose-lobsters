/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.posts

import androidx.compose.runtime.Stable
import dev.msfjarvis.claw.model.LinkMetadata
import dev.msfjarvis.claw.model.UIPost

@Stable
interface PostActions {
  fun viewPost(postId: String, postUrl: String, commentsUrl: String)

  fun viewComments(postId: String)

  fun viewCommentsPage(commentsUrl: String)

  fun toggleSave(post: UIPost)

  suspend fun getComments(postId: String): UIPost

  suspend fun getLinkMetadata(url: String): LinkMetadata
}
