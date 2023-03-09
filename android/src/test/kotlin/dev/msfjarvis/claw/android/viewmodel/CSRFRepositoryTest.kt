/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class CSRFRepositoryTest : FunSpec() {
  private val server = MockWebServer()

  init {
    test("Correctly extracts CSRF token").config(coroutineTestScope = true) {
      val repo =
        CSRFRepository(
          OkHttpClient.Builder().build(),
          Dispatchers.Default,
          server.url("/").toString(),
        )
      repo.extractToken() shouldBe
        "OZWykgFemPVeOSNmB53-ccKXe458X7xCInO1-qzFU6nk_9RCSrSQqS9OPmA5_pyy8qD3IYAIZ7XfAM3gdhJpkQ"
    }
  }

  override suspend fun beforeSpec(spec: Spec) {
    super.beforeSpec(spec)
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
