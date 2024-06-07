/*
 * Copyright © 2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.data.store

import com.slack.eithernet.successOrNull
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.database.local.NewestPosts
import dev.msfjarvis.claw.database.local.NewestPostsQueries
import dev.msfjarvis.claw.database.local.ReadPostsQueries
import dev.msfjarvis.claw.database.local.SavedPostQueries
import dev.msfjarvis.claw.model.PostConverter
import dev.msfjarvis.claw.model.UIPost
import dev.msfjarvis.claw.model.fromNewestPosts
import javax.inject.Inject
import org.mobilenativefoundation.store.store5.Converter
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.StoreBuilder

@Suppress("Unused")
class NewestPostsStore
@Inject
constructor(
  private val api: LobstersApi,
  private val postsDatabase: NewestPostsQueries,
  private val savedDatabase: SavedPostQueries,
  private val readDatabase: ReadPostsQueries,
  private val postConverter: PostConverter,
) {
  private val fetcher: Fetcher<Int, NetworkPostsResult> =
    Fetcher.of { key -> api.getHottestPosts(key) }

  private val sourceOfTruth: SourceOfTruth<Int, List<NewestPosts>, List<UIPost>> =
    SourceOfTruth.of(
      nonFlowReader = { page ->
        postsDatabase.getPage(page).executeAsList().map(UIPost::fromNewestPosts)
      },
      writer = { page, posts ->
        postsDatabase.transaction {
          posts.forEach { post ->
            postsDatabase.addPost(
              post.copy(
                page = page,
                isRead = readDatabase.isRead(post.shortId).executeAsOne(),
                isSaved = savedDatabase.isSaved(post.shortId).executeAsOne(),
              )
            )
          }
        }
      },
      delete = { page -> postsDatabase.deletePage(page) },
      deleteAll = { postsDatabase.deleteAllPages() },
    )

  private val converter =
    Converter.Builder<NetworkPostsResult, List<NewestPosts>, List<UIPost>>()
      .fromOutputToLocal { posts -> posts.map { postConverter.toNewestPost(it, 0) } }
      .fromNetworkToLocal { result ->
        result
          .successOrNull()
          ?.map { postConverter.toNewestPost(post = it, page = 0, isRead = false, isSaved = false) }
          .orEmpty()
      }
      .build()

  val store =
    StoreBuilder.from(fetcher = fetcher, sourceOfTruth = sourceOfTruth, converter = converter)
      .build()
}
