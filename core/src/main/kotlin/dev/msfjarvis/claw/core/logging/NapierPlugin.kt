/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.logging

import android.app.Application
import dev.msfjarvis.claw.core.injection.AppPlugin
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

@ContributesIntoSet(AppScope::class)
@Inject
class NapierPlugin() : AppPlugin {
  override fun apply(application: Application) {
    Napier.base(DebugAntilog())
  }
}
