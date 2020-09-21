package dev.msfjarvis.lobsters.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.msfjarvis.lobsters.data.source.TodoDatabase

@InstallIn(SingletonComponent::class)
@Module
object PersistenceModule {
  @Provides
  fun provideItemsDatabase(@ApplicationContext context: Context): TodoDatabase {
    return Room.databaseBuilder(context, TodoDatabase::class.java, "data.db")
      .fallbackToDestructiveMigration().build()
  }
}
