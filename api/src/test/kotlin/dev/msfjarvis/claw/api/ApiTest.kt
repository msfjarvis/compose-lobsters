/*
 * Copyright © 2022-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api

import com.slack.eithernet.ApiResult.Success
import com.slack.eithernet.test.newEitherNetController
import dev.msfjarvis.claw.api.util.Tags
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.model.LobstersPostDetails
import dev.msfjarvis.claw.model.User
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf

class ApiTest : FunSpec() {
  private val wrapper = ApiWrapper(newEitherNetController())
  private val api
    get() = wrapper.api

  init {
    test("api gets correct number of items") {
      val posts = api.getHottestPosts(1)
      posts.shouldBeTypeOf<Success<List<LobstersPost>>>()
      posts.value shouldHaveSize 25
    }

    test("posts with no urls") {
      val posts = api.getHottestPosts(1)
      posts.shouldBeTypeOf<Success<List<LobstersPost>>>()
      val commentsOnlyPosts = posts.value.asSequence().filter { it.url.isEmpty() }.toSet()
      commentsOnlyPosts shouldHaveSize 2
    }

    test("post details with comments") {
      val postDetails = api.getPostDetails("tdfoqh")
      postDetails.shouldBeTypeOf<Success<LobstersPostDetails>>()
      postDetails.value.comments shouldHaveSize 7
    }

    test("get user details") {
      val user = api.getUser("msfjarvis")
      user.shouldBeTypeOf<Success<User>>()
      user.value.username shouldBe "msfjarvis"
    }

    test("get posts by single tag") {
      var tags = Tags()
      tags.addTag("meta")
      val posts = api.getPostsByTags(tags, 1)
      posts.shouldBeTypeOf<Success<List<LobstersPost>>>()
      posts.value shouldHaveSize 25
      posts.value[0].tags.contains("meta")
    }

    test("get posts by multiple tags") {
      var tags = Tags()
      tags.addTag("programming")
      tags.addTag("rust")
      val posts = api.getPostsByTags(tags, 1)
      posts.shouldBeTypeOf<Success<List<LobstersPost>>>()
      posts.value shouldHaveSize 25
      posts.value[0].tags.contains("programming") or posts.value[0].tags.contains("rust")
    }
  }
}
