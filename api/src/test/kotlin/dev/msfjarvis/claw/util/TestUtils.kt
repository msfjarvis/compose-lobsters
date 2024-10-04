/*
 * Copyright © 2021-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.util

import com.google.common.truth.Truth.assertThat
import java.io.File
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
object TestUtils {
  fun getResource(path: String): String {
    // Load the JSON response
    val uri =
      requireNotNull(javaClass.classLoader) { "if this is null something has gone very wrong" }
        .getResource(path)
    val file = File(uri.path)
    return String(file.readBytes())
  }

  inline fun <reified T> assertIs(value: Any?) {
    contract { returns() implies (value is T) }
    assertThat(value).isInstanceOf(T::class.java)
  }
}
