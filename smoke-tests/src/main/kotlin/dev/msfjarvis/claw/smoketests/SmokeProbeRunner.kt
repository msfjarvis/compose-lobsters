/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.smoketests

import com.slack.eithernet.ApiResult
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.model.Comment
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.model.LobstersPostDetails
import dev.msfjarvis.claw.model.Tag
import dev.zacsweers.metro.Inject

class SmokeProbeResult(private val errors: List<String>) {
  val isSuccess: Boolean
    get() = errors.isEmpty()

  fun errors(): List<String> = errors
}

@Inject
class SmokeProbeRunner(private val api: LobstersApi) {
  suspend fun run(): SmokeProbeResult {
    val failures = mutableListOf<String>()

    val hottest = probe("getHottestPosts(1)", failures) { api.getHottestPosts(1) }
    val hottestPosts = hottest?.also { validatePosts("getHottestPosts(1)", it, failures) }.orEmpty()

    probe("getNewestPosts(1)", failures) { api.getNewestPosts(1) }
      ?.also { validatePosts("getNewestPosts(1)", it, failures) }

    probe("getTags()", failures) { api.getTags() }?.also { validateTags(it, failures) }

    probe("getCSRFToken()", failures) { api.getCSRFToken() }
      ?.also { token -> if (token.value.isBlank()) failures += "getCSRFToken(): blank token" }

    val discoveredPost = hottestPosts.firstOrNull {
      it.shortId.isNotBlank() && it.submitter.isNotBlank()
    }
    if (discoveredPost == null) {
      failures += "discovery: no post with usable id and username"
      return SmokeProbeResult(failures)
    }

    probe("getUser(${discoveredPost.submitter})", failures) {
        api.getUser(discoveredPost.submitter)
      }
      ?.also { user -> validateUser(discoveredPost.submitter, user.username, failures) }

    probe("getPostDetails(${discoveredPost.shortId})", failures) {
        api.getPostDetails(discoveredPost.shortId)
      }
      ?.also { details ->
        validatePostDetails(discoveredPost.shortId, discoveredPost.submitter, details, failures)
      }

    return SmokeProbeResult(failures)
  }

  private suspend fun <T : Any> probe(
    name: String,
    failures: MutableList<String>,
    block: suspend () -> ApiResult<T, Unit>,
  ): T? {
    return when (val result = block()) {
      is ApiResult.Success -> {
        println("PASS $name")
        result.value
      }
      is ApiResult.Failure -> {
        val message = "FAIL $name: $result"
        println(message)
        failures += message
        null
      }
    }
  }
}

private fun validatePosts(name: String, posts: List<LobstersPost>, failures: MutableList<String>) {
  if (posts.isEmpty()) {
    failures += "$name: empty posts"
    return
  }
  posts.forEachIndexed { index, post ->
    if (post.shortId.isBlank()) failures += "$name: post[$index] blank id"
    if (post.title.isBlank()) failures += "$name: post[$index] blank title"
    if (post.url.isBlank()) failures += "$name: post[$index] blank url"
  }
}

private fun validateTags(tags: List<Tag>, failures: MutableList<String>) {
  if (tags.isEmpty()) {
    failures += "getTags(): empty tags"
    return
  }
  tags.forEachIndexed { index, tag ->
    if (tag.tag.isBlank()) failures += "getTags(): tag[$index] blank name"
  }
}

private fun validateUser(
  expectedUsername: String,
  actualUsername: String,
  failures: MutableList<String>,
) {
  if (actualUsername.isBlank()) failures += "getUser($expectedUsername): blank username"
  if (actualUsername.isNotBlank() && actualUsername != expectedUsername) {
    failures += "getUser($expectedUsername): returned username '$actualUsername'"
  }
}

private fun validatePostDetails(
  expectedShortId: String,
  expectedSubmitter: String,
  details: LobstersPostDetails,
  failures: MutableList<String>,
) {
  if (details.shortId.isBlank()) failures += "getPostDetails($expectedShortId): blank id"
  if (details.shortId.isNotBlank() && details.shortId != expectedShortId) {
    failures += "getPostDetails($expectedShortId): returned id '${details.shortId}'"
  }
  if (details.title.isBlank()) failures += "getPostDetails($expectedShortId): blank title"
  if (details.submitter.isNotBlank() && details.submitter != expectedSubmitter) {
    failures += "getPostDetails($expectedShortId): returned submitter '${details.submitter}'"
  }
  details.comments.forEachIndexed { index, comment ->
    validateComment(expectedShortId, comment, index, failures)
  }
}

private fun validateComment(
  expectedShortId: String,
  comment: Comment,
  index: Int,
  failures: MutableList<String>,
) {
  if (comment.shortId.isBlank())
    failures += "getPostDetails($expectedShortId): comment[$index] blank id"
  if (comment.user.isBlank())
    failures += "getPostDetails($expectedShortId): comment[$index] blank username"
  if (comment.score < 0)
    failures += "getPostDetails($expectedShortId): comment[$index] invalid score ${comment.score}"
}
