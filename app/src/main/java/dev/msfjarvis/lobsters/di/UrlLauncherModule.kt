package dev.msfjarvis.lobsters.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dev.msfjarvis.lobsters.urllauncher.UrlLauncher
import dev.msfjarvis.lobsters.urllauncher.UrlLauncherImpl

@InstallIn(ActivityComponent::class)
@Module
object UrlLauncherModule {
  @Provides
  fun provideUrlLauncher(@ActivityContext context: Context): UrlLauncher {
    return UrlLauncherImpl(context)
  }
}
