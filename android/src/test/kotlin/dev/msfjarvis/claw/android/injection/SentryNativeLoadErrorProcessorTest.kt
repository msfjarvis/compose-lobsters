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
import java.util.concurrent.atomic.AtomicInteger
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
        "supported_abis" to listOf("arm64-v8a", "armeabi-v7a"),
        "process_64bit" to true,
        "installer" to "com.android.vending",
      )
    }

    val processed = processor.process(event, Hint())

    assertThat(processed).isSameInstanceAs(event)
    assertThat(event.throwableMechanism).isSameInstanceAs(wrapped)
    assertThat(event.getThrowable()).isSameInstanceAs(throwable)
    assertThat(event.getThrowable()!!.cause).isSameInstanceAs(original)
    assertThat(event.extras).containsEntry("native_load_error.source_dir", "/data/app/base.apk")
    assertThat(event.extras)
      .containsEntry(
        "native_load_error.split_source_dirs",
        listOf("/data/app/config.arm64_v8a.apk"),
      )
    assertThat(event.extras)
      .containsEntry("native_load_error.native_library_dir", "/data/app/lib/arm64")
    assertThat(event.extras).containsEntry("native_load_error.process_64bit", true)
    assertThat(event.extras).containsEntry("native_load_error.installer", "com.android.vending")
    assertThat(event.extras!!["native_load_error.supported_abis"])
      .isEqualTo(listOf("arm64-v8a", "armeabi-v7a"))
    assertThat(event.extras)
      .containsEntry("native_load_error.linker_exception", original.toString())
    assertThat(calls.get()).isEqualTo(1)
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
