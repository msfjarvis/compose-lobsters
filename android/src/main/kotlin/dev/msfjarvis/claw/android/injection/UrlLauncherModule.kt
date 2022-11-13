/*
 * Copyright © 2021 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.injection

import android.content.Context
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import dev.msfjarvis.claw.injection.scopes.AppScope

@Module
@ContributesTo(AppScope::class)
object UrlLauncherModule {
  @Provides
  fun provideUrlLauncher(context: Context): UrlLauncher {
    return UrlLauncher(context)
  }
}
