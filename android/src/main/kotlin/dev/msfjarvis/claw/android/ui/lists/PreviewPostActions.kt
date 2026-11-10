/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.lists

import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.model.LinkMetadata
import dev.msfjarvis.claw.model.UIPost

internal val TEST_POST_ACTIONS =
  object : PostActions {
    override fun viewPost(postId: String, postUrl: String, commentsUrl: String) = Unit

    override fun viewComments(postId: String, commentsUrl: String) = Unit

    override fun viewCommentsPage(post: UIPost) = Unit

    override fun toggleSave(post: UIPost) = Unit

    override fun share(post: UIPost) = Unit

    override fun shareComment(commentId: String) = Unit

    override fun isPostRead(post: UIPost): Boolean = true

    override fun isPostSaved(post: UIPost): Boolean = true

    override suspend fun getLinkMetadata(url: String): LinkMetadata = LinkMetadata("", "")
  }
