/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.database.injection

import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dev.msfjarvis.claw.database.LobstersDatabase
import dev.msfjarvis.claw.database.local.PostCommentsQueries
import dev.msfjarvis.claw.database.local.SavedPostQueries

@Module
@ContributesTo(ApplicationScope::class)
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
}
