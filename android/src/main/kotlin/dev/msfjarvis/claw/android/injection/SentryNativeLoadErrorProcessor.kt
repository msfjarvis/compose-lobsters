/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("DenyListedApi")

package dev.msfjarvis.claw.android.injection

import android.app.Application
import android.os.Build
import android.os.Process
import io.sentry.EventProcessor
import io.sentry.Hint
import io.sentry.SentryEvent
import java.io.File
import java.util.Collections
import java.util.IdentityHashMap
import java.util.zip.ZipFile

/** Captures best-effort diagnostics for the libsqlite3x.so linker error. */
internal class SentryNativeLoadErrorProcessor(private val diagnostics: () -> Map<String, Any?>) :
  EventProcessor {

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

internal fun nativeLoadDiagnostics(application: Application): Map<String, Any?> = buildMap {
  val appInfo = application.applicationInfo
  val sourceDir = readDiagnostic("source_dir", this) { appInfo.sourceDir }
  val splitDirs =
    readDiagnostic("split_source_dirs", this) {
      appInfo.splitSourceDirs?.toList().orEmpty()
    }
  put("split_count", splitDirs?.size ?: 0)

  val nativeLibraryDir = readDiagnostic("native_library_dir", this) { appInfo.nativeLibraryDir }
  val supportedAbis = readDiagnostic("supported_abis", this) { Build.SUPPORTED_ABIS.toList() }
  val is64Bit = readDiagnostic("process_64bit", this) { Process.is64Bit() }
  val compatibleAbis =
    if (supportedAbis != null && is64Bit != null) {
      processCompatibleAbis(supportedAbis, is64Bit).also { put("process_compatible_abis", it) }
    } else {
      null
    }

  if (sourceDir != null && splitDirs != null && supportedAbis != null) {
    putAll(
      apkNativeLibraryDiagnostics(
        listOf(sourceDir) + splitDirs,
        supportedAbis,
        compatibleAbis.orEmpty(),
      )
    )
  } else {
    put("apk_native_library_inventory_status", "unavailable_missing_apk_or_abi_diagnostics")
  }

  if (nativeLibraryDir != null) putAll(extractedLibraryDiagnostics(nativeLibraryDir))
  runCatching { put("installer", installerPackage(application)) }
    .onFailure { put("installer_read_error", it.javaClass.simpleName) }
}

private fun <T> readDiagnostic(
  key: String,
  destination: MutableMap<String, Any?>,
  read: () -> T,
): T? =
  runCatching(read)
    .onFailure { destination["${key}_read_error"] = it.javaClass.simpleName }
    .getOrNull()
    .also { value ->
      if (value != null) destination[key] = value
    }

internal fun processCompatibleAbis(
  supportedAbis: List<String>,
  process64Bit: Boolean,
): List<String> = supportedAbis.filter { abi ->
  val abiIs64Bit = abi in setOf("arm64-v8a", "x86_64", "mips64", "riscv64")
  abiIs64Bit == process64Bit
}

/** Lists matching ZIP entries only; it never extracts or reads native library contents. */
internal fun apkNativeLibraryDiagnostics(
  apkPaths: List<String>,
  supportedAbis: List<String>,
  processCompatibleAbis: List<String>,
): Map<String, Any?> = buildMap {
  val inventory = apkPaths.map { path ->
    runCatching {
      ZipFile(path).use { zip ->
        val availableEntries =
          zip
            .entries()
            .asSequence()
            .map { it.name }
            .filter { entry ->
              entry.matches(Regex("lib/[^/]+/libsqlite3x\\.so"))
            }
            .toList()
        val supportedEntries = availableEntries.filter { entry ->
          entry.substringAfter("lib/").substringBefore('/') in supportedAbis
        }
        val processCompatibleEntries = availableEntries.filter { entry ->
          entry.substringAfter("lib/").substringBefore('/') in processCompatibleAbis
        }
        mapOf(
          "apk_path" to path,
          "status" to if (availableEntries.isEmpty()) "absent" else "present",
          "available_entries" to availableEntries,
          "supported_entries" to supportedEntries,
          "process_compatible_entries" to processCompatibleEntries,
        )
      }
    }
      .getOrElse { failure ->
        mapOf(
          "apk_path" to path,
          "status" to "unreadable",
          "read_error" to failure.javaClass.simpleName,
        )
      }
  }
  put("apk_native_library_inventory", inventory)
  put("apk_native_library_inventory_status", "complete")
}

private fun extractedLibraryDiagnostics(nativeLibraryDir: String): Map<String, Any?> = buildMap {
  val library = File(nativeLibraryDir, "libsqlite3x.so")
  runCatching {
    put("extracted_library_path", library.path)
    put("extracted_library_exists", library.exists())
    put("extracted_library_readable", library.canRead())
    if (library.exists() && library.isFile) put("extracted_library_size_bytes", library.length())
  }
    .onFailure { put("extracted_library_stat_error", it.javaClass.simpleName) }
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
