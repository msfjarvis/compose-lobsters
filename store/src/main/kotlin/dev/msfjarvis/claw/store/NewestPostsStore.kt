/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.store

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.slack.eithernet.ApiResult
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.core.injection.DatabaseDispatcher
import dev.msfjarvis.claw.core.injection.IODispatcher
import dev.msfjarvis.claw.database.LobstersDatabase
import dev.msfjarvis.claw.database.local.CachedNewestPost
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.store.utils.toCachedNewest
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder

class NewestPostsStore
@Inject
constructor(
  private val api: LobstersApi,
  private val database: LobstersDatabase,
  @IODispatcher private val ioDispatcher: CoroutineDispatcher,
  @DatabaseDispatcher private val dbDispatcher: CoroutineDispatcher,
) :
  Store<Int, List<CachedNewestPost>> by StoreBuilder.from<
      Int, List<LobstersPost>, List<CachedNewestPost>
    >(
      fetcher =
        Fetcher.of { key ->
          withContext(ioDispatcher) {
            when (val result = api.getNewestPosts(key)) {
              is ApiResult.Success -> result.value
              is ApiResult.Failure.NetworkFailure -> throw result.error
              is ApiResult.Failure.UnknownFailure -> throw result.error
              is ApiResult.Failure.HttpFailure,
              is ApiResult.Failure.ApiFailure ->
                throw IOException("API returned an invalid response")
            }
          }
        },
      sourceOfTruth =
        SourceOfTruth.of(
          reader = { page ->
            database.cachedNewestPostQueries.getPage(page).asFlow().mapToList(dbDispatcher)
          },
          writer = { page, items ->
            database.transaction {
              items
                .map { it.toCachedNewest(page) }
                .forEach { database.cachedNewestPostQueries.insertPost(it) }
            }
          },
          delete = { page -> database.cachedNewestPostQueries.clearPage(page) },
          deleteAll = { database.cachedNewestPostQueries.deleteAll() },
        ),
    )
    .build()
