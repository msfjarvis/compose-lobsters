/*
 * Copyright © 2021-2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.posts

import dev.msfjarvis.claw.database.local.SavedPost
import dev.msfjarvis.claw.model.LinkMetadata
import dev.msfjarvis.claw.model.LobstersPostDetails

interface PostActions {
  fun viewPost(postUrl: String, commentsUrl: String)
  fun viewComments(postId: String)
  fun viewCommentsPage(commentsUrl: String)
  fun toggleSave(post: SavedPost)
  suspend fun getComments(postId: String): LobstersPostDetails
  suspend fun getLinkMetadata(url: String): LinkMetadata
}
