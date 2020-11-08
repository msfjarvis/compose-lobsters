package dev.msfjarvis.lobsters.injection

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dev.msfjarvis.lobsters.ui.urllauncher.UrlLauncher
import dev.msfjarvis.lobsters.ui.urllauncher.UrlLauncherImpl

@Module
@InstallIn(ActivityComponent::class)
object UrlLauncherModule {
  @Provides
  fun provideUrlLauncher(@ActivityContext context: Context): UrlLauncher {
    return UrlLauncherImpl(context)
  }
}
