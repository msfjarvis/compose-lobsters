/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.ui

import com.google.common.truth.Truth.assertWithMessage
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.common.BuildConfig
import org.junit.Test

// Pure destination policy, run on Android to retain the original Uri parsing contract.
// java.net.URI and HttpUrl normalize/reject inputs differently; neither is a drop-in replacement.
class LobstersDestinationTest {
  @Test
  fun exactConfiguredHostAndHttpSchemesAcceptShortIdsWithoutDependingOnSlugOrSuffix() {
    for (url in
      listOf(
        "${LobstersApi.BASE_URL}/s/abc123",
        "https://lobste.rs/s/abc123/",
        "http://lobste.rs/s/abc123/a_title",
        "https://lobste.rs/s/abc123/title?utm_source=test#comment-42",
        "https://lobste.rs:443/s/abc123",
        "http://user:password@lobste.rs:80/s/abc123",
        "https://lobste.rs/s/abc123?next=/s/other",
      )) {
      assertWithMessage(url)
        .that(rewriteUrlIfLobstersPost(url))
        .isEqualTo("${BuildConfig.DEEPLINK_SCHEME}://comments/abc123")
    }
  }

  @Test
  fun androidDecodedPathAndTolerantParsingRemainUnchanged() {
    val cases =
      mapOf(
        "https://lobste.rs/%73/abc123" to "abc123",
        "https://lobste.rs/s%2Fabc123/title" to "abc123",
        "https://lobste.rs/s/%61bc123/title" to "abc123",
        "https://lobste.rs/s/abc123%2Frest" to "abc123",
        "https://lobste%2Ers/s/abc123" to "abc123",
        "https://lobste.rs/s/a+b" to "a+b",
        "https://lobste.rs/s/a%252Fb" to "a%2Fb",
        "https://lobste.rs/s/white space" to "white space",
        "https://lobste.rs/s/🙂" to "🙂",
      )
    cases.forEach { (url, id) ->
      assertWithMessage(url)
        .that(rewriteUrlIfLobstersPost(url))
        .isEqualTo("${BuildConfig.DEEPLINK_SCHEME}://comments/$id")
    }
  }

  @Test
  fun caseSensitiveSchemeHostAndPathRejectNearMatches() {
    for (url in
      listOf(
        "HTTPS://lobste.rs/s/abc123",
        "https://LOBSTE.RS/s/abc123",
        "https://www.lobste.rs/s/abc123",
        "https://lobste.rs.example.com/s/abc123",
        "https://lobste.rs@evil.example/s/abc123",
        "https://evil.example/?next=https://lobste.rs/s/abc123",
        "https://lobste.rs./s/abc123",
        "https://lobste.rs/S/abc123",
        "https://lobste.rs/story/abc123",
        "https://lobste.rs/x/s/abc123",
        "https://lobste.rs//s/abc123",
        "ftp://lobste.rs/s/abc123",
        "javascript:alert('https://lobste.rs/s/abc123')",
        "mailto:lobste.rs/s/abc123",
        "${BuildConfig.DEEPLINK_SCHEME}://comments/abc123",
      )) {
      assertWithMessage(url).that(rewriteUrlIfLobstersPost(url)).isEqualTo(url)
    }
  }

  @Test
  fun emptyIdRelativeOpaqueMalformedAndNonStoryDestinationsStayByteForByteUnchanged() {
    for (url in
      listOf(
        "",
        "not a URL",
        "/s/abc123",
        "//lobste.rs/s/abc123",
        "http:lobste.rs/s/abc123",
        "https:/lobste.rs/s/abc123",
        "https:///s/abc123",
        "https://",
        "https://lobste.rs",
        "https://lobste.rs/s",
        "https://lobste.rs/s/",
        "https://lobste.rs/s//title",
        "https://lobste.rs/s/?id=abc123",
        "https://lobste.rs/s/#abc123",
        "https://lobste.rs/s/%2Fabc123",
        "https://example.com/s/abc123?query=unchanged#fragment",
        "https://lobste.rs/u/alice",
        "#comment-42",
      )) {
      assertWithMessage(url).that(rewriteUrlIfLobstersPost(url)).isEqualTo(url)
    }
  }
}
