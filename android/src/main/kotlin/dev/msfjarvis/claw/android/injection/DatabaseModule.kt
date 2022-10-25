/*
 * Copyright © 2021-2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.injection

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.msfjarvis.claw.database.LobstersDatabase
import dev.msfjarvis.claw.database.local.createDatabase

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

  @Provides
  fun provideDatabase(@ApplicationContext context: Context): LobstersDatabase {
    return createDatabase(context)
  }
}
