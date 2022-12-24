/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.sync

import dev.msfjarvis.claw.sync.plugins.configureHTTP
import dev.msfjarvis.claw.sync.plugins.configureMonitoring
import dev.msfjarvis.claw.sync.plugins.configureRouting
import dev.msfjarvis.claw.sync.plugins.configureSecurity
import dev.msfjarvis.claw.sync.plugins.configureSerialization
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
  embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
    .start(wait = true)
}

fun Application.module() {
  configureSerialization()
  configureMonitoring()
  configureHTTP()
  configureSecurity()
  configureRouting()
}
