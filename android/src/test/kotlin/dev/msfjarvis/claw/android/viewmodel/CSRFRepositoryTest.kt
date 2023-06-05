/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class CSRFRepositoryTest {
  @Test
  fun `correctly extracts CSRF token`() = runTest {
    val repo =
      CSRFRepository(
        OkHttpClient.Builder().build(),
        Dispatchers.Default,
        server.url("/").toString(),
      )
    assertThat(repo.extractToken())
      .isEqualTo(
        "OZWykgFemPVeOSNmB53-ccKXe458X7xCInO1-qzFU6nk_9RCSrSQqS9OPmA5_pyy8qD3IYAIZ7XfAM3gdhJpkQ"
      )
  }

  companion object {
    private val server = MockWebServer()

    @JvmStatic
    @BeforeAll
    fun setup() {
      val dispatcher =
        object : Dispatcher() {
          override fun dispatch(request: RecordedRequest): MockResponse {
            return when (val path = request.path) {
              "/" ->
                MockResponse()
                  .setResponseCode(200)
                  .setBody(
                    javaClass.classLoader!!
                      .getResourceAsStream("csrf_page.html")
                      .readAllBytes()
                      .decodeToString(),
                  )
              else -> error("Invalid path: $path")
            }
          }
        }
      server.dispatcher = dispatcher
    }
  }
}
