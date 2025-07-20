/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.injection

import android.content.Context
import com.deliveryhero.whetstone.activity.ActivityScope
import com.squareup.anvil.annotations.optional.ForScope
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Module
import dev.zacsweers.metro.Provides

@Module
@ContributesTo(ActivityScope::class)
object UrlLauncherModule {
  @Provides
  fun provideUrlLauncher(@ForScope(ActivityScope::class) context: Context): UrlLauncher {
    return UrlLauncher(context)
  }
}
