/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.injection

import com.google.common.truth.Truth.assertThat
import io.sentry.Hint
import io.sentry.SentryEvent
import io.sentry.exception.ExceptionMechanismException
import io.sentry.protocol.Mechanism
import java.nio.file.Files
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout

@Suppress("UnstableApiUsage")
class SentryNativeLoadErrorProcessorTest {
  @Test
  fun `enriches event for a wrapped linker error without replacing throwable`() {
    val original = UnsatisfiedLinkError("dlopen failed: library \"libsqlite3x.so\" not found")
    val throwable = IllegalStateException("database setup failed", original)
    val wrapped = ExceptionMechanismException(Mechanism(), throwable, Thread.currentThread())
    val event = SentryEvent(wrapped)
    val calls = AtomicInteger()
    val processor = SentryNativeLoadErrorProcessor {
      calls.incrementAndGet()
      mapOf(
        "source_dir" to "/data/app/base.apk",
        "split_source_dirs" to listOf("/data/app/config.arm64_v8a.apk"),
        "native_library_dir" to "/data/app/lib/arm64",
        "supported_abis" to ["arm64-v8a", "armeabi-v7a"],
        "process_64bit" to true,
        "installer" to "com.android.vending",
      )
    }

    val processed = processor.process(event, Hint())

    assertThat(processed).isSameInstanceAs(event)
    assertThat(event.throwableMechanism).isSameInstanceAs(wrapped)
    assertThat(event.getThrowable()).isSameInstanceAs(throwable)
    assertThat(event.getThrowable()?.cause).isSameInstanceAs(original)
    assertThat(event.extras).containsEntry("native_load_error.source_dir", "/data/app/base.apk")
    assertThat(event.extras)
      .containsEntry(
        "native_load_error.split_source_dirs",
        ["/data/app/config.arm64_v8a.apk"],
      )
    assertThat(event.extras)
      .containsEntry("native_load_error.native_library_dir", "/data/app/lib/arm64")
    assertThat(event.extras).containsEntry("native_load_error.process_64bit", true)
    assertThat(event.extras).containsEntry("native_load_error.installer", "com.android.vending")
    assertThat(event.extras?.get("native_load_error.supported_abis"))
      .isEqualTo(["arm64-v8a", "armeabi-v7a"])
    assertThat(event.extras)
      .containsEntry("native_load_error.linker_exception", original.toString())
    assertThat(calls.get()).isEqualTo(1)
  }

  @Test
  fun `reports matching native library entries in each APK without extracting them`() {
    val apk = Files.createTempFile("native-library-fixture", ".apk")
    ZipOutputStream(Files.newOutputStream(apk)).use { zip ->
      zip.putNextEntry(ZipEntry("lib/arm64-v8a/libsqlite3x.so"))
      zip.closeEntry()
      zip.putNextEntry(ZipEntry("lib/armeabi-v7a/libsqlite3x.so"))
      zip.closeEntry()
      zip.putNextEntry(ZipEntry("lib/x86_64/libother.so"))
      zip.closeEntry()
    }

    val diagnostics =
      apkNativeLibraryDiagnostics(
        listOf(apk.toString()),
        listOf("arm64-v8a", "x86_64"),
        listOf("arm64-v8a"),
      )

    assertThat(diagnostics["apk_native_library_inventory_status"]).isEqualTo("complete")
    assertThat(diagnostics["apk_native_library_inventory"])
      .isEqualTo(
        listOf(
          mapOf(
            "apk_path" to apk.toString(),
            "status" to "present",
            "available_entries" to
              listOf("lib/arm64-v8a/libsqlite3x.so", "lib/armeabi-v7a/libsqlite3x.so"),
            "supported_entries" to listOf("lib/arm64-v8a/libsqlite3x.so"),
            "process_compatible_entries" to listOf("lib/arm64-v8a/libsqlite3x.so"),
          )
        )
      )
    Files.deleteIfExists(apk)
  }

  @Test
  fun `distinguishes absent library from unreadable APK and includes every path`() {
    val apk = Files.createTempFile("native-library-absent", ".apk")
    ZipOutputStream(Files.newOutputStream(apk)).use { zip ->
      zip.putNextEntry(ZipEntry("assets/placeholder"))
      zip.closeEntry()
    }
    val missingApk = apk.resolveSibling("missing-native-library.apk")

    val inventory =
      apkNativeLibraryDiagnostics(
        listOf(apk.toString(), missingApk.toString()),
        listOf("arm64-v8a"),
        listOf("arm64-v8a"),
      )["apk_native_library_inventory"]

    assertThat(inventory)
      .isEqualTo(
        listOf(
          mapOf(
            "apk_path" to apk.toString(),
            "status" to "absent",
            "available_entries" to emptyList<String>(),
            "supported_entries" to emptyList<String>(),
            "process_compatible_entries" to emptyList<String>(),
          ),
          mapOf(
            "apk_path" to missingApk.toString(),
            "status" to "unreadable",
            "read_error" to "NoSuchFileException",
          ),
        )
      )
    Files.deleteIfExists(apk)
  }

  @Test
  fun `process compatible ABIs follow process bitness instead of the first supported ABI`() {
    val abis = listOf("armeabi-v7a", "arm64-v8a", "x86_64")

    assertThat(processCompatibleAbis(abis, true)).containsExactly("arm64-v8a", "x86_64").inOrder()
    assertThat(processCompatibleAbis(abis, false)).containsExactly("armeabi-v7a")
  }

  @Test
  fun `empty APK list produces an explicit empty inventory`() {
    val diagnostics =
      apkNativeLibraryDiagnostics(emptyList(), listOf("arm64-v8a"), listOf("arm64-v8a"))

    assertThat(diagnostics["apk_native_library_inventory"]).isEqualTo(emptyList<Any?>())
    assertThat(diagnostics["apk_native_library_inventory_status"]).isEqualTo("complete")
  }

  @Test
  fun `retains linker exception when diagnostics fail`() {
    val original = UnsatisfiedLinkError("libsqlite3x.so not found")
    val event = SentryEvent(original)
    val processor = SentryNativeLoadErrorProcessor { error("diagnostics unavailable") }

    processor.process(event, Hint())

    assertThat(event.extras)
      .containsEntry("native_load_error.linker_exception", original.toString())
    assertThat(event.getThrowable()).isSameInstanceAs(original)
  }

  @Test
  fun `ignores events without a linker error`() {
    val event = SentryEvent(IllegalStateException("database setup failed"))
    var diagnosticsCalled = false
    val processor = SentryNativeLoadErrorProcessor {
      diagnosticsCalled = true
      mapOf("source_dir" to "/should/not/be-added")
    }

    processor.process(event, Hint())

    assertThat(event.extras).isNull()
    assertThat(diagnosticsCalled).isFalse()
  }

  @Test
  @Timeout(1)
  fun `terminates on a causal cycle without a linker error`() {
    val first = IllegalStateException("first")
    val second = IllegalArgumentException("second")
    first.initCause(second)
    second.initCause(first)
    val event = SentryEvent(first)
    var diagnosticsCalled = false
    val processor = SentryNativeLoadErrorProcessor {
      diagnosticsCalled = true
      emptyMap()
    }

    processor.process(event, Hint())

    assertThat(event.extras).isNull()
    assertThat(diagnosticsCalled).isFalse()
  }

  @Test
  fun `ignores event without a throwable`() {
    val event = SentryEvent()
    var diagnosticsCalled = false
    val processor = SentryNativeLoadErrorProcessor {
      diagnosticsCalled = true
      emptyMap()
    }

    processor.process(event, Hint())

    assertThat(event.getThrowable()).isNull()
    assertThat(event.extras).isNull()
    assertThat(diagnosticsCalled).isFalse()
  }
}
