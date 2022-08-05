package dev.msfjarvis.claw.android.injection

import com.chimbori.crux.Crux
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object MetadataExtractorModule {
  @Provides
  fun provideCrux(
    okHttpClient: OkHttpClient,
  ): Crux {
    return Crux(okHttpClient = okHttpClient)
  }
}
