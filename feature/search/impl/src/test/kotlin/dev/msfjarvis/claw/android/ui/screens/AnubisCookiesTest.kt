/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.screens

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class AnubisCookiesTest {
  @Test
  fun `finds new JWT clearance and ignores other cookies`() {
    val cookies = "lobsters_trap=session; techaro.lol-anubis-auth-site=aaa.bbb.ccc"
    assertThat(newAnubisClearanceCookie(cookies, null))
      .isEqualTo("techaro.lol-anubis-auth-site=aaa.bbb.ccc")
  }

  @Test
  fun `does not accept stale missing malformed or verification cookies`() {
    val old = "techaro.lol-anubis-auth-site=aaa.bbb.ccc"
    assertThat(newAnubisClearanceCookie(old, old)).isNull()
    assertThat(newAnubisClearanceCookie(null, null)).isNull()
    assertThat(newAnubisClearanceCookie("techaro.lol-anubis-auth=aaa.bbb.ccc", null)).isNull()
    assertThat(newAnubisClearanceCookie("techaro.lol-anubis-auth-site=bad.jwt", null)).isNull()
    assertThat(newAnubisClearanceCookie("techaro.lol-anubis-cookie-verification=aaa.bbb.ccc", null))
      .isNull()
    assertThat(
        newAnubisClearanceCookie(
          "techaro.lol-anubis-auth-site=aaa.bbb.ccc",
          "techaro.lol-anubis-auth-site=old.jwt.sig",
        )
      )
      .isNotNull()
  }
}
