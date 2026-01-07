/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.database.injection

import dev.msfjarvis.claw.database.LobstersDatabase
import dev.msfjarvis.claw.database.local.PostCommentsQueries
import dev.msfjarvis.claw.database.local.ReadPostsQueries
import dev.msfjarvis.claw.database.local.SavedPostQueries
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(AppScope::class)
object QueriesModule {

  @Provides
  @SingleIn(AppScope::class)
  fun provideSavedPostsQueries(@InternalDatabaseApi database: LobstersDatabase): SavedPostQueries {
    return database.savedPostQueries
  }

  @Provides
  @SingleIn(AppScope::class)
  fun providePostCommentsQueries(
    @InternalDatabaseApi database: LobstersDatabase
  ): PostCommentsQueries {
    return database.postCommentsQueries
  }

  @Provides
  @SingleIn(AppScope::class)
  fun provideReadPostsQueries(@InternalDatabaseApi database: LobstersDatabase): ReadPostsQueries {
    return database.readPostsQueries
  }
}
