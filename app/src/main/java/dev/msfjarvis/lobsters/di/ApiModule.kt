package dev.msfjarvis.lobsters.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dev.msfjarvis.lobsters.api.ApiClient
import dev.msfjarvis.lobsters.api.LobstersApi

@InstallIn(ActivityComponent::class)
@Module
object ApiModule {
  @Provides
  fun provideLobstersApi(): LobstersApi {
    return ApiClient.getClient("https://lobste.rs")
  }
}
