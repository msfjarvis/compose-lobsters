/*
 * Copyright © 2021-2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android

import android.app.Application
import com.deliveryhero.whetstone.Whetstone
import com.deliveryhero.whetstone.app.ApplicationComponentOwner
import com.deliveryhero.whetstone.app.ContributesAppInjector
import dev.msfjarvis.claw.core.injection.AppPlugin
import javax.inject.Inject

@ContributesAppInjector(generateAppComponent = true)
class ClawApplication : Application(), ApplicationComponentOwner {

  override val applicationComponent by
    lazy(LazyThreadSafetyMode.NONE) { GeneratedApplicationComponent.create(this) }

  @Inject lateinit var plugins: Set<@JvmSuppressWildcards AppPlugin>

  override fun onCreate() {
    Whetstone.inject(this)
    super.onCreate()
    plugins.forEach { plugin -> plugin.apply(this) }
  }
}
