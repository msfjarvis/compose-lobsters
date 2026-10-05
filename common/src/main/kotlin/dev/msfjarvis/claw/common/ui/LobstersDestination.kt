/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.ui

import androidx.core.net.toUri
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.common.BuildConfig

// Keep Android Uri's case-sensitive scheme/host and decoded path behavior.
internal fun rewriteUrlIfLobstersPost(url: String): String {
  val lobstersUri = LobstersApi.BASE_URL.toUri()
  return try {
    val uri = url.toUri()
    if (
      uri.scheme in setOf("http", "https") &&
        uri.host == lobstersUri.host &&
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
