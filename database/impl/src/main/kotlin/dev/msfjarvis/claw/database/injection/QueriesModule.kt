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

@BindingContainer
@ContributesTo(AppScope::class)
object QueriesModule {

  @Provides
  fun provideSavedPostsQueries(@InternalDatabaseApi database: LobstersDatabase): SavedPostQueries {
    return database.savedPostQueries
  }

  @Provides
  fun providePostCommentsQueries(
    @InternalDatabaseApi database: LobstersDatabase
  ): PostCommentsQueries {
    return database.postCommentsQueries
  }

  @Provides
  fun provideReadPostsQueries(@InternalDatabaseApi database: LobstersDatabase): ReadPostsQueries {
    return database.readPostsQueries
  }
}
