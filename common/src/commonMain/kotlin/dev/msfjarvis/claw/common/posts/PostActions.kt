package dev.msfjarvis.claw.common.posts

import dev.msfjarvis.claw.database.local.SavedPost

interface PostActions {
  fun viewPost(postUrl: String, commentsUrl: String)
  fun viewComments(postId: String)
  fun toggleSave(post: SavedPost)
}
