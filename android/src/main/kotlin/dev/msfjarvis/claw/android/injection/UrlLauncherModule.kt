/*
 * Copyright © 2021 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.injection

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher

@Module
@InstallIn(ActivityComponent::class)
object UrlLauncherModule {
  @Provides
  fun provideUrlLauncher(@ActivityContext context: Context): UrlLauncher {
    return UrlLauncher(context)
  }
}
