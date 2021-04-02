package dev.msfjarvis.lobsters.injection

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.msfjarvis.lobsters.data.local.SavedPost
import dev.msfjarvis.lobsters.database.LobstersDatabase
import dev.msfjarvis.lobsters.model.TagsAdapter
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

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
  fun providesLobstersDatabase(sqlDriver: SqlDriver, tagsAdapter: TagsAdapter): LobstersDatabase {
    return LobstersDatabase(
      sqlDriver,
      SavedPost.Adapter(tagsAdapter),
    )
  }
}
