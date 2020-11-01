package dev.msfjarvis.lobsters.injection

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.msfjarvis.lobsters.data.source.PostsDatabase

@Module
@InstallIn(ActivityComponent::class)
object PersistenceModule {

  @Provides
  fun providePostsDatabase(@ApplicationContext context: Context): PostsDatabase {
    return Room.databaseBuilder(context, PostsDatabase::class.java, "posts.db")
      .fallbackToDestructiveMigration()
      .build()
  }
}
