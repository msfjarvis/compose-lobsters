/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api

import com.fleeksoft.ksoup.Ksoup
import com.slack.eithernet.ApiResult.Companion.success
import com.slack.eithernet.test.EitherNetController
import com.slack.eithernet.test.enqueue
import dev.burnoo.kspoon.Kspoon
import dev.msfjarvis.claw.api.converters.CSRFTokenConverter
import dev.msfjarvis.claw.model.LobstersPostDetails
import dev.msfjarvis.claw.model.User
import dev.msfjarvis.claw.util.TestUtils.getResource
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody

class ApiWrapper(controller: EitherNetController<LobstersApi>) {
  private val kspoon = Kspoon {
    parse = { html -> Ksoup.parse(html, baseUri = LobstersApi.BASE_URL) }
    coerceInputValues = true
  }
  private val postsPage: PostsPage = kspoon.parse<PostsPage>(getResource("hottest_page.html"))
  private val postDetails: LobstersPostDetails =
    kspoon.parse(getResource("post_details_tdfoqh.html"))
  val upvotedPostDetails: LobstersPostDetails =
    kspoon.parse(getResource("post_details_upvoted.html"))
  private val user: User = kspoon.parse(getResource("msfjarvis.html"))
  private val tags: TagsPage = kspoon.parse(getResource("tags.html"))

  val api = controller.api
  val authenticatedApi = AuthenticatedLobstersApi(api)

  init {
    controller.enqueue(LobstersApi::getHottestPosts) { success(postsPage) }
    controller.enqueue(LobstersApi::getHottestPosts) { success(postsPage) }
    controller.enqueue(LobstersApi::getHottestPosts) { success(postsPage) }
    controller.enqueue(LobstersApi::getNewestPosts) { success(postsPage) }
    controller.enqueue(LobstersApi::getPostDetails) { success(postDetails) }
    controller.enqueue(LobstersApi::getUser) { success(user) }
    controller.enqueue(LobstersApi::getTags) { success(tags) }
    controller.enqueue(LobstersApi::getCSRFToken) {
      success(
        CSRFTokenConverter.convert(
          getResource("csrf_page.html").toResponseBody("text/html".toMediaType())
        )
      )
    }
    controller.enqueue(LobstersApi::getCSRFToken) {
      success(
        CSRFTokenConverter.convert(
          getResource("csrf_page.html").toResponseBody("text/html".toMediaType())
        )
      )
    }
    controller.enqueue(LobstersApi::getCSRFToken) {
      success(
        CSRFTokenConverter.convert(
          getResource("csrf_page.html").toResponseBody("text/html".toMediaType())
        )
      )
    }
    controller.enqueue(LobstersApi::getCSRFToken) {
      success(
        CSRFTokenConverter.convert(
          getResource("csrf_page.html").toResponseBody("text/html".toMediaType())
        )
      )
    }
    controller.enqueue(LobstersApi::upvoteComment) { success(Unit) }
    controller.enqueue(LobstersApi::unvoteComment) { success(Unit) }
    controller.enqueue(LobstersApi::getReplyForm) {
      success(
        dev.msfjarvis.claw.api.converters.ReplyFormConverter.convert(
          getResource("reply_form.html").toResponseBody("text/html".toMediaType())
        )
      )
    }
    controller.enqueue(LobstersApi::postReply) { success(Unit) }
  }
}
