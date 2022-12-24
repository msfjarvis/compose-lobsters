/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.sync.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.hsts.HSTS
import io.ktor.server.plugins.httpsredirect.HttpsRedirect

fun Application.configureHTTP() {
  if (!developmentMode) {
    install(HttpsRedirect) {
      // The port to redirect to. By default 443, the default HTTPS port.
      sslPort = 443
      // 301 Moved Permanently, or 302 Found redirect.
      permanentRedirect = true
    }
    install(HSTS) { includeSubDomains = true }
  }
}
