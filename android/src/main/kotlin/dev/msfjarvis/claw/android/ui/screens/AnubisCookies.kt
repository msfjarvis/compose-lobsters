/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.screens

private const val CLEARANCE_PREFIX = "techaro.lol-anubis-auth-"

/** Returns only a newly issued clearance cookie, never an existing stale WebView cookie. */
internal fun newAnubisClearanceCookie(cookies: String?, previousCookies: String?): String? {
  val previous = previousCookies.orEmpty().split(';').map(String::trim).toSet()
  return cookies.orEmpty().split(';').asSequence().map(String::trim).firstOrNull { cookie ->
    val name = cookie.substringBefore('=')
    val value = cookie.substringAfter('=', "")
    val jwtParts = value.split('.')
    name.startsWith(CLEARANCE_PREFIX) &&
      name.length > CLEARANCE_PREFIX.length &&
      cookie !in previous &&
      jwtParts.size == 3 &&
      jwtParts.all { part ->
        part.isNotEmpty() && part.all { it.isLetterOrDigit() || it == '_' || it == '-' }
      }
  }
}
