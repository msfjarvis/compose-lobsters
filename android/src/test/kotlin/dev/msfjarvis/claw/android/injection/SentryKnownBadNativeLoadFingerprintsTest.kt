/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.injection

import com.google.common.truth.Truth.assertThat
import io.sentry.SentryEvent
import io.sentry.protocol.Device
import io.sentry.protocol.OperatingSystem
import org.junit.jupiter.api.Test

class SentryKnownBadNativeLoadFingerprintsTest {
  @Test
  fun `suppresses each exact Android 11 scanner model and build combination`() {
    val models =
      listOf("Mi 9X", "Huawei Enjoy 9s", "Motorola One Vision", "vivo S1", "Samsung Galaxy A70")
    val builds =
      listOf(
        "scutum-user 11 LMY47D 5080180 release-keys",
        "norma-user 11 LMY47D 5080180 release-keys",
        "mensa-user 11 LMY47D 5080180 release-keys",
        "andromeda-user 11 LMY47D 5080180 release-keys",
      )

    models.forEach { model ->
      builds.forEach { build ->
        assertThat(isKnownBadNativeLoadEvent(scannerEvent(model, build))).isTrue()
      }
    }
  }

  @Test
  fun `suppresses the exact reported Pixel and explicit emulator fingerprints`() {
    assertThat(
        isKnownBadNativeLoadEvent(
          scannerEvent(
            "Pixel 6 Pro",
            "sdk_phone_arm64-eng 12 SP2A.220505.008 eng.ubuntu.20231126.034832 test-keys",
          )
        )
      )
      .isTrue()
    assertThat(
        isKnownBadNativeLoadEvent(
          scannerEvent(
            "sdk_gphone64_x86_64",
            "sdk_gphone64_x86_64-userdebug 12 SE1A.220826.008 10564458 dev-keys",
          )
        )
      )
      .isTrue()
  }

  @Test
  fun `retains other linker errors and unrelated events on known scanner devices`() {
    assertThat(
        scannerEvent("Mi 9X", "scutum-user 11 LMY47D 5080180 release-keys", "libc.so")
          .let(::isKnownBadNativeLoadEvent)
      )
      .isFalse()
    assertThat(
        scannerEvent(
            "Mi 9X",
            "scutum-user 11 LMY47D 5080180 release-keys",
            error = IllegalStateException("failure"),
          )
          .let(::isKnownBadNativeLoadEvent)
      )
      .isFalse()
    assertThat(
        scannerEvent(
            "Mi 9X",
            "scutum-user 11 LMY47D 5080180 release-keys",
            error =
              UnsatisfiedLinkError(
                "dlopen failed: cannot locate symbol 'sqlite3_open' in libsqlite3x.so"
              ),
          )
          .let(::isKnownBadNativeLoadEvent)
      )
      .isFalse()
  }

  @Test
  fun `retains the target error on an unknown build or different physical device`() {
    assertThat(
        isKnownBadNativeLoadEvent(scannerEvent("Mi 9X", "scutum-user 12 future-build release-keys"))
      )
      .isFalse()
    assertThat(isKnownBadNativeLoadEvent(scannerEvent("Samsung Galaxy A70", "different build")))
      .isFalse()
    assertThat(
        isKnownBadNativeLoadEvent(
          scannerEvent(
            "Pixel 6",
            "sdk_phone_arm64-eng 12 SP2A.220505.008 eng.ubuntu.20231126.034832 test-keys",
          )
        )
      )
      .isFalse()
  }

  @Test
  fun `incomplete device or operating system contexts are retained`() {
    val exactError = "dlopen failed: library \"libsqlite3x.so\" not found"
    val withoutDevice = SentryEvent(UnsatisfiedLinkError(exactError))
    withoutDevice.contexts.setOperatingSystem(
      OperatingSystem().apply { build = "scutum-user 11 LMY47D 5080180 release-keys" }
    )
    val withoutOs = SentryEvent(UnsatisfiedLinkError(exactError))
    withoutOs.contexts.setDevice(Device().apply { model = "Mi 9X" })

    assertThat(isKnownBadNativeLoadEvent(withoutDevice)).isFalse()
    assertThat(isKnownBadNativeLoadEvent(withoutOs)).isFalse()
  }

  private fun scannerEvent(
    model: String,
    build: String,
    library: String = "libsqlite3x.so",
    error: Throwable = UnsatisfiedLinkError("dlopen failed: library \"$library\" not found"),
  ): SentryEvent =
    SentryEvent(error).apply {
      contexts.setDevice(Device().apply { this.model = model })
      contexts.setOperatingSystem(OperatingSystem().apply { this.build = build })
    }
}
