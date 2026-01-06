/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android

import android.app.Application
import dev.zacsweers.metrox.android.MetroAppComponentFactory

/**
 * Custom variant of [android.app.AppComponentFactory] that skips [ClawApplication] init when the
 * app is running in a backup process for Android Auto Backup. In all normal cases, it will defer to
 * [MetroAppComponentFactory].
 */
@Suppress("unused")
class BackupSafeAppComponentFactory : MetroAppComponentFactory() {

  override fun instantiateApplicationCompat(cl: ClassLoader, className: String): Application {
    if (isBackupAgentProcess()) {
      return Application()
    }
    return super.instantiateApplicationCompat(cl, className)
  }

  private fun isBackupAgentProcess(): Boolean {
    return try {
      val processName = Application.getProcessName()
      processName?.contains("backup") == true
    } catch (_: Exception) {
      false
    }
  }
}
