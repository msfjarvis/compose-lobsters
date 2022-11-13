/*
 * Copyright © 2021-2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.database.injection

import android.content.Context
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.deliveryhero.whetstone.ForScope
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dev.msfjarvis.claw.database.LobstersDatabase
import dev.msfjarvis.claw.database.local.SavedPost
import dev.msfjarvis.claw.database.model.TagsAdapter

@Module
@ContributesTo(ApplicationScope::class)
object DatabaseModule {

  private const val LOBSTERS_DATABASE_NAME = "SavedPosts.db"

  @Provides
  fun provideDatabase(@ForScope(ApplicationScope::class) context: Context): LobstersDatabase {
    val driver = AndroidSqliteDriver(LobstersDatabase.Schema, context, LOBSTERS_DATABASE_NAME)
    return LobstersDatabase(driver, SavedPost.Adapter(IntColumnAdapter, TagsAdapter()))
  }
}
