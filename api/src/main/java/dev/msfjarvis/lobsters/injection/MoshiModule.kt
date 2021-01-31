package dev.msfjarvis.lobsters.injection

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.msfjarvis.lobsters.model.Submitter
import dev.zacsweers.moshix.reflect.MetadataKotlinJsonAdapterFactory

@Module
@InstallIn(SingletonComponent::class)
object MoshiModule {
  @Provides
  @Reusable
  fun provideMoshi(): Moshi {
    return Moshi.Builder()
      .add(MetadataKotlinJsonAdapterFactory())
      .build()
  }

  @OptIn(ExperimentalStdlibApi::class)
  @Provides
  @Reusable
  fun provideSubmitterJsonAdapter(moshi: Moshi): JsonAdapter<Submitter> {
    return moshi.adapter()
  }
}
