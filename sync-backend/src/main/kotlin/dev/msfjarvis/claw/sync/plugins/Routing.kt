/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:OptIn(KtorExperimentalLocationsAPI::class)

package dev.msfjarvis.claw.sync.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.locations.KtorExperimentalLocationsAPI
import io.ktor.server.locations.Location
import io.ktor.server.locations.Locations
import io.ktor.server.locations.get
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.doublereceive.DoubleReceive
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
  install(StatusPages) {
    exception<AuthenticationException> { call, _ -> call.respond(HttpStatusCode.Unauthorized) }
    exception<AuthorizationException> { call, _ -> call.respond(HttpStatusCode.Forbidden) }
  }
  install(Locations) {}
  install(DoubleReceive)
  install(AutoHeadResponse)

  routing {
    get("/") { call.respondText("Hello World!") }
    get<MyLocation> {
      call.respondText("Location: name=${it.name}, arg1=${it.arg1}, arg2=${it.arg2}")
    }
    // Register nested routes
    get<Type.Edit> { call.respondText("Inside $it") }
    get<Type.List> { call.respondText("Inside $it") }
  }
}

class AuthenticationException : RuntimeException()

class AuthorizationException : RuntimeException()

@Location("/location/{name}")
class MyLocation(val name: String, val arg1: Int = 42, val arg2: String = "default")

@Location("/type/{name}")
data class Type(val name: String) {
  @Location("/edit") data class Edit(val type: Type)

  @Location("/list/{page}") data class List(val type: Type, val page: Int)
}
