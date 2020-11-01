package dev.msfjarvis.lobsters.injection

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dev.msfjarvis.lobsters.api.ApiClient
import dev.msfjarvis.lobsters.api.LobstersApi

@InstallIn(ActivityComponent::class)
@Module
object ApiModule {
  const val LOBSTERS_URL = "https://lobste.rs"

  @Provides
  fun provideLobstersApi(): LobstersApi {
    return ApiClient.getClient(LOBSTERS_URL)
  }
}
