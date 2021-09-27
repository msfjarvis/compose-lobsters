package dev.msfjarvis.claw.android.injection

import android.content.Context
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.msfjarvis.claw.database.LobstersDatabase
import dev.msfjarvis.claw.database.local.DriverFactory
import dev.msfjarvis.claw.database.local.SavedPost
import dev.msfjarvis.claw.database.model.TagsAdapter

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

  @Provides
  fun provideDriverFactory(@ApplicationContext context: Context): SqlDriver {
    return DriverFactory(context).createDriver()
  }

  @Provides
  fun provideSavedPostsAdapter(): SavedPost.Adapter {
    return SavedPost.Adapter(TagsAdapter())
  }

  @Provides
  fun provideDatabase(
    driver: SqlDriver,
    adapter: SavedPost.Adapter,
  ): LobstersDatabase {
    return LobstersDatabase(driver, adapter)
  }
}
