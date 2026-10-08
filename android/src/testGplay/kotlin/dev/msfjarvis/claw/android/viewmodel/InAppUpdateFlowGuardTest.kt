/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class InAppUpdateFlowGuardTest {
  @Test
  fun `only latest asynchronous check is accepted`() {
    val guard = InAppUpdateFlowGuard()

    val staleCheck = guard.beginCheck()
    val currentCheck = guard.beginCheck()

    assertThat(guard.isLatestCheck(staleCheck)).isFalse()
    assertThat(guard.isLatestCheck(currentCheck)).isTrue()
  }

  @Test
  fun `flow in flight suppresses repeated requests until activity result`() {
    val guard = InAppUpdateFlowGuard()

    guard.markUpdateRequested()
    assertThat(guard.beginFlow()).isTrue()
    assertThat(guard.beginFlow()).isFalse()

    guard.onFlowResult()

    assertThat(guard.isFlowInFlight).isFalse()
    assertThat(guard.hasRequestedUpdate).isTrue()
    assertThat(guard.beginFlow()).isTrue()
  }

  @Test
  fun `launch failure resets flow and requested-update guards`() {
    val guard = InAppUpdateFlowGuard()
    guard.markUpdateRequested()
    assertThat(guard.beginFlow()).isTrue()

    guard.onLaunchFailed()

    assertThat(guard.isFlowInFlight).isFalse()
    assertThat(guard.hasRequestedUpdate).isFalse()
    assertThat(guard.beginFlow()).isTrue()
  }
}
