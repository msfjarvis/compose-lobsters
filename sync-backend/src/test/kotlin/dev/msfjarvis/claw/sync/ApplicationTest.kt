/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.sync

import dev.msfjarvis.claw.sync.plugins.configureRouting
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication

class ApplicationTest : FunSpec() {
  init {
    test("testRoot") {
      testApplication {
        application { configureRouting() }
        client.get("/").apply {
          status shouldBe HttpStatusCode.OK
          bodyAsText() shouldBe "Hello World!"
        }
      }
    }
  }
}
