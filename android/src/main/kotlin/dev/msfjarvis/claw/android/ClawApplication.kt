/*
 * Copyright © 2021-2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android

import android.app.Application
import dev.msfjarvis.claw.core.injection.AppPlugin
import dev.msfjarvis.claw.injection.Components
import dev.msfjarvis.claw.injection.scopes.AppScope
import javax.inject.Inject
import tangle.inject.TangleGraph
import tangle.inject.TangleScope

@TangleScope(AppScope::class)
class ClawApplication : Application() {

  @Inject lateinit var plugins: Set<@JvmSuppressWildcards AppPlugin>

  override fun onCreate() {
    super.onCreate()
    val component = DaggerAppComponent.factory().create(this)
    Components.add(component)
    TangleGraph.add(component)
    TangleGraph.inject(this)
    plugins.forEach { plugin -> plugin.apply(this) }
  }
}
