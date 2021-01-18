package dev.msfjarvis.lobsters.injection

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.msfjarvis.lobsters.data.source.PostsDatabase
import dev.msfjarvis.lobsters.data.source.PostsDatabase_Migrations

@Module
@InstallIn(ViewModelComponent::class)
object PersistenceModule {

  @Provides
  fun providePostsDatabase(@ApplicationContext context: Context): PostsDatabase {
    return Room.databaseBuilder(context, PostsDatabase::class.java, "posts.db")
      .addMigrations(*PostsDatabase_Migrations.build())
      .build()
  }
}
