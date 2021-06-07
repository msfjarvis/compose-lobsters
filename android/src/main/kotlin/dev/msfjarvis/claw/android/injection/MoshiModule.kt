package dev.msfjarvis.claw.android.injection

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.zacsweers.moshix.reflect.MetadataKotlinJsonAdapterFactory

@Module
@InstallIn(SingletonComponent::class)
object MoshiModule {
  @Provides
  @Reusable
  fun provideMoshi(): Moshi {
    return Moshi.Builder().add(MetadataKotlinJsonAdapterFactory()).build()
  }
}
