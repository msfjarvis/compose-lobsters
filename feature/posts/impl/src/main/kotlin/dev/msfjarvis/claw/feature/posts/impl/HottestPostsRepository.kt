/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.posts.impl

import com.slack.eithernet.ApiResult
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.feature.posts.api.HottestPostsSource
import dev.msfjarvis.claw.model.UIPost
import dev.msfjarvis.claw.model.toUIPost
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import java.util.concurrent.CancellationException

@Inject
@ContributesBinding(AppScope::class, binding = binding<HottestPostsSource>())
class HottestPostsRepository(
  private val api: LobstersApi,
  private val cachedPosts: CachedRemotePostsRepository,
) : HottestPostsSource {
  override suspend fun getHottestPosts(): List<UIPost> {
    return when (val result = api.getHottestPosts(1)) {
      is ApiResult.Success ->
        result.value
          .map { it.toUIPost() }
          .also { posts ->
            try {
              cachedPosts.savePosts(posts)
            } catch (cancellation: CancellationException) {
              throw cancellation
            } catch (_: Exception) {
              // Widget rendering must not fail because caching did.
            }
          }
      else -> cachedPostsOrEmpty { cachedPosts.getCachedPosts() }
    }
  }
}

internal suspend fun <T> cachedPostsOrEmpty(loader: suspend () -> List<T>): List<T> =
  try {
    loader()
  } catch (cancellation: CancellationException) {
    throw cancellation
  } catch (_: Exception) {
    emptyList()
  }
