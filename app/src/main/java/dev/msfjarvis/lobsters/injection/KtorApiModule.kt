package dev.msfjarvis.lobsters.injection

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dev.msfjarvis.lobsters.data.api.KtorLobstersApi
import dev.msfjarvis.lobsters.data.api.LobstersApi

@Module
@InstallIn(ActivityComponent::class)
abstract class KtorApiModule {
  @Binds abstract fun bindLobstersApi(realApi: KtorLobstersApi): LobstersApi
}
