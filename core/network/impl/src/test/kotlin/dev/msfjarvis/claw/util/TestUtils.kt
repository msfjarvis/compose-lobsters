/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.util

import com.google.common.truth.Truth.assertThat
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
object TestUtils {
  fun getResource(path: String): String {
    val resource =
      requireNotNull(javaClass.classLoader) { "if this is null something has gone very wrong" }
        .getResource(path)
    return resource.readText()
  }

  inline fun <reified T> assertIs(value: Any?) {
    contract { returns() implies (value is T) }
    assertThat(value).isInstanceOf(T::class.java)
  }
}
