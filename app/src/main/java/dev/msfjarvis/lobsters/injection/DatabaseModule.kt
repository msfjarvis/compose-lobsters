package dev.msfjarvis.lobsters.injection

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.msfjarvis.lobsters.data.local.LobstersPost
import dev.msfjarvis.lobsters.database.LobstersDatabase
import dev.msfjarvis.lobsters.model.Submitter
import dev.msfjarvis.lobsters.model.SubmitterAdapter
import dev.msfjarvis.lobsters.model.TagsAdapter
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

  @Provides
  @Reusable
  fun providesSubmitterAdapter(jsonAdapter: JsonAdapter<Submitter>): SubmitterAdapter {
    return SubmitterAdapter(jsonAdapter)
  }

  @Provides
  @Reusable
  fun providesTagsAdapter(): TagsAdapter {
    return TagsAdapter()
  }

  @Provides
  @Singleton
  fun providesSqlDriver(@ApplicationContext context: Context): SqlDriver {
    return AndroidSqliteDriver(LobstersDatabase.Schema, context, "SavedPosts.db")
  }

  @Provides
  @Singleton
  fun providesLobstersDatabase(
    sqlDriver: SqlDriver,
    submitterAdapter: SubmitterAdapter,
    tagsAdapter: TagsAdapter
  ): LobstersDatabase {
    return LobstersDatabase(
      sqlDriver,
      LobstersPost.Adapter(submitterAdapter, tagsAdapter)
    )
  }
}
