/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.database.injection

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import app.cash.sqldelight.logs.LogSqliteDriver
import dev.msfjarvis.claw.database.LobstersDatabase
import dev.msfjarvis.claw.database.local.PostComments
import dev.msfjarvis.claw.database.local.SavedPost
import dev.msfjarvis.claw.database.model.CSVAdapter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.github.aakira.napier.Napier
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import io.requery.android.database.sqlite.SQLiteDatabase

@BindingContainer
@ContributesTo(AppScope::class)
object DatabaseModule {

  private const val LOBSTERS_DATABASE_NAME = "SavedPosts.db"

  @Provides
  @InternalDatabaseApi
  @SingleIn(AppScope::class)
  fun provideDatabase(context: Context): LobstersDatabase {
    System.loadLibrary(SQLiteDatabase.LIBRARY_NAME)
    val driver =
      LogSqliteDriver(
        AndroidSqliteDriver(
          schema = LobstersDatabase.Schema,
          context = context,
          name = LOBSTERS_DATABASE_NAME,
          factory = RequerySQLiteOpenHelperFactory(),
          callback =
            object : AndroidSqliteDriver.Callback(LobstersDatabase.Schema) {
              override fun onConfigure(db: SupportSQLiteDatabase) {
                super.onConfigure(db)
                db.enableWriteAheadLogging()
              }
            },
        )
      ) { message ->
        Napier.d(tag = "SQLDelightQuery", message = message)
      }
    return LobstersDatabase(
      driver = driver,
      PostCommentsAdapter = PostComments.Adapter(CSVAdapter()),
      SavedPostAdapter = SavedPost.Adapter(IntColumnAdapter, CSVAdapter()),
    )
  }
}
