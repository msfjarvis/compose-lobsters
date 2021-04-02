package dev.msfjarvis.lobsters.injection

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MoshiModule {
  @Provides
  @Reusable
  fun provideMoshi(): Moshi {
    return Moshi.Builder().build()
  }
}
