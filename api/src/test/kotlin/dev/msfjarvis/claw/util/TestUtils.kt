/*
 * Copyright © 2021 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.util

import java.io.File

object TestUtils {
  fun getJson(path: String): String {
    // Load the JSON response
    val uri = javaClass.classLoader.getResource(path)
    val file = File(uri.path)
    return String(file.readBytes())
  }
}
