/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.sync.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.authentication
import io.ktor.server.auth.basic
import io.ktor.server.auth.form
import io.ktor.server.auth.principal
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureSecurity() {

  authentication {
    basic(name = "myauth1") {
      realm = "Ktor Server"
      validate { credentials ->
        if (credentials.name == credentials.password) {
          UserIdPrincipal(credentials.name)
        } else {
          null
        }
      }
    }

    form(name = "myauth2") {
      userParamName = "user"
      passwordParamName = "password"
      challenge {
        /**/
      }
    }
  }

  routing {
    authenticate("myauth1") {
      get("/protected/route/basic") {
        val principal = call.principal<UserIdPrincipal>()
        call.respondText("Hello ${principal?.name}")
      }
    }
    authenticate("myauth2") {
      get("/protected/route/form") {
        val principal = call.principal<UserIdPrincipal>()
        call.respondText("Hello ${principal?.name}")
      }
    }
  }
}
