/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android

import androidx.test.platform.app.InstrumentationRegistry
import dev.msfjarvis.claw.android.zipline.AndroidZiplineParserClient
import dev.msfjarvis.claw.api.ParserRuntimeConfig
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Test

class ZiplineRegressionTest {
  @Test
  fun loadsEmbeddedParser(): Unit = runBlocking {
    val client =
      AndroidZiplineParserClient(
        InstrumentationRegistry.getInstrumentation().targetContext.applicationContext,
        OkHttpClient(),
        ParserRuntimeConfig(manifestUrl = "", verifySignatures = false),
      )
    try {
      client.service().parseCsrfToken("<meta name=\"csrf-token\" content=\"test\">")
    } finally {
      client.close()
    }
  }
}
