/*
 * Copyright © 2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:OptIn(ExperimentalStoreApi::class)

package dev.msfjarvis.claw.data.store

import com.slack.eithernet.ApiResult
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.model.UIPost
import org.mobilenativefoundation.store.core5.ExperimentalStoreApi
import org.mobilenativefoundation.store.core5.InsertionStrategy
import org.mobilenativefoundation.store.core5.StoreData
import org.mobilenativefoundation.store.core5.StoreKey

typealias NetworkPostsResult = ApiResult<List<LobstersPost>, Unit>

data class PagingKey(
  override val page: Int,
  override val size: Int = 25,
  override val insertionStrategy: InsertionStrategy = InsertionStrategy.APPEND,
  override val filters: List<StoreKey.Filter<*>>? = null,
  override val sort: StoreKey.Sort? = null,
) : StoreKey.Collection.Page

sealed class PagingResult {
  data class Post(val post: UIPost) : StoreData.Single<String>, PagingResult() {
    override val id: String = post.shortId
  }

  data class Page(val posts: List<Post>) : StoreData.Collection<String, Post>, PagingResult() {
    override val items: List<Post>
      get() = posts

    override fun copyWith(items: List<Post>): StoreData.Collection<String, Post> =
      copy(posts = posts)

    override fun insertItems(
      strategy: InsertionStrategy,
      items: List<Post>,
    ): StoreData.Collection<String, Post> {
      return when (strategy) {
        InsertionStrategy.APPEND -> {
          val updatedItems = items.toMutableList()
          updatedItems.addAll(posts)
          copyWith(items = updatedItems)
        }
        InsertionStrategy.PREPEND -> {
          val updatedItems = posts.toMutableList()
          updatedItems.addAll(items)
          copyWith(items = updatedItems)
        }
        InsertionStrategy.REPLACE -> {
          copyWith(items = posts)
        }
      }
    }
  }
}
