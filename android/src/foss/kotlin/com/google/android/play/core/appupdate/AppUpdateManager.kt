/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.android.play.core.appupdate

import android.content.Context

/** Empty FOSS shim for the Play Core manager injected by the shared app graph. */
class AppUpdateManager internal constructor()

/** Empty FOSS shim matching the Play Core manager factory used by the shared app graph. */
object AppUpdateManagerFactory {
  @Suppress("UNUSED_PARAMETER") fun create(context: Context): AppUpdateManager = AppUpdateManager()
}
