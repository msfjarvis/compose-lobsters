/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.injection

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.os.Process
import io.sentry.EventProcessor
import io.sentry.Hint
import io.sentry.SentryEvent
import java.util.Collections
import java.util.IdentityHashMap

/**
 * I've been consistently seeing the same libsqlite3x.so load error reported that nobody with a real
 * device seems to have complained about. I really wanna know what is so fucked about this specific
 * device that causes this to happen so this [EventProcessor] will capture some debugging
 * information.
 */
internal class SentryNativeLoadErrorProcessor(private val diagnostics: () -> Map<String, Any?>) :
  EventProcessor {

  @SuppressLint("DenyListedApi") // runCatching is fine for this need.
  override fun process(event: SentryEvent, hint: Hint): SentryEvent {
    val linkerError = event.getThrowable().findCause<UnsatisfiedLinkError>() ?: return event

    runCatching {
      event.setExtra("native_load_error.linker_exception", linkerError.toString())
    }
    runCatching {
      diagnostics().forEach { (key, value) ->
        if (value != null) event.setExtra("native_load_error.$key", value)
      }
    }
    return event
  }
}

@SuppressLint("DenyListedApi") // runCatching is fine for this need.
internal fun nativeLoadDiagnostics(application: Application): Map<String, Any?> = buildMap {
  runCatching { put("source_dir", application.applicationInfo.sourceDir) }
  runCatching { put("split_source_dirs", application.applicationInfo.splitSourceDirs?.toList()) }
  runCatching { put("native_library_dir", application.applicationInfo.nativeLibraryDir) }
  runCatching { put("supported_abis", Build.SUPPORTED_ABIS.toList()) }
  runCatching { put("process_64bit", Process.is64Bit()) }
  runCatching { put("installer", installerPackage(application)) }
}

private fun installerPackage(application: Application): String? =
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
    application.packageManager.getInstallSourceInfo(application.packageName).installingPackageName
  } else {
    @Suppress("DEPRECATION")
    application.packageManager.getInstallerPackageName(application.packageName)
  }

private inline fun <reified T : Throwable> Throwable?.findCause(): T? {
  val seen: MutableSet<Throwable> = Collections.newSetFromMap(IdentityHashMap())
  var current = this
  while (current != null && seen.add(current)) {
    if (current is T) return current
    current = current.cause
  }
  return null
}
