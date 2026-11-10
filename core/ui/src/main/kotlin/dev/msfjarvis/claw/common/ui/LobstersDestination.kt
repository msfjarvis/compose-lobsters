/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.ui

import androidx.core.net.toUri
import dev.msfjarvis.claw.core.ui.BuildConfig

/** Rewrites Lobsters story links into app deep links. */
fun rewriteUrlIfLobstersPost(url: String): String {
  val lobstersHost = "lobste.rs"
  return try {
    val uri = url.toUri()
    if (
      uri.scheme in setOf("http", "https") &&
        uri.host == lobstersHost &&
        uri.path?.startsWith("/s/") == true
    ) {
      val pathSegments = uri.path?.split("/").orEmpty()
      if (pathSegments.size >= 3) {
        val shortId = pathSegments[2]
        if (shortId.isNotEmpty()) {
          return "${BuildConfig.DEEPLINK_SCHEME}://comments/$shortId"
        }
      }
    }
    url
  } catch (_: Exception) {
    url
  }
}
