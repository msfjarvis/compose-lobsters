/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.injection

import io.sentry.SentryEvent
import java.util.Collections
import java.util.IdentityHashMap

/** Exact scanner image fingerprints; these suppress telemetry only, not the application crash. */
private val knownBadScannerFingerprints: Set<Pair<String, String>> = buildSet {
  val android11Models =
    setOf("Mi 9X", "Huawei Enjoy 9s", "Motorola One Vision", "vivo S1", "Samsung Galaxy A70")
  val scannerBuilds =
    setOf(
      "scutum-user 11 LMY47D 5080180 release-keys",
      "norma-user 11 LMY47D 5080180 release-keys",
      "mensa-user 11 LMY47D 5080180 release-keys",
      "andromeda-user 11 LMY47D 5080180 release-keys",
    )
  android11Models.forEach { model -> scannerBuilds.forEach { build -> add(model to build) } }

  add(
    "Pixel 6 Pro" to "sdk_phone_arm64-eng 12 SP2A.220505.008 eng.ubuntu.20231126.034832 test-keys"
  )
  add("sdk_gphone64_x86_64" to "sdk_gphone64_x86_64-userdebug 12 SE1A.220826.008 10564458 dev-keys")
}

/** Return true only for the known libsqlite3x scanner crash fingerprints. */
internal fun isKnownBadNativeLoadEvent(event: SentryEvent): Boolean {
  val linkerError = event.getThrowable().findCause<UnsatisfiedLinkError>() ?: return false
  if (linkerError.message != "dlopen failed: library \"libsqlite3x.so\" not found") return false

  val deviceModel = event.contexts.device?.model ?: return false
  val osBuild = event.contexts.operatingSystem?.build ?: return false
  return deviceModel to osBuild in knownBadScannerFingerprints
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
