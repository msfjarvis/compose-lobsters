/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android

import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import dev.msfjarvis.claw.android.ui.navigation.Comments
import dev.msfjarvis.claw.android.ui.navigation.Hottest
import dev.msfjarvis.claw.android.ui.navigation.Newest
import dev.msfjarvis.claw.android.ui.navigation.Saved
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AppDeepLinkMatchersTest {
  private val matchers = appDeepLinkMatchers("claw")

  private fun match(uri: String) = matchDeepLink(DeepLinkRequest(uri), matchers)

  @Test
  fun matchesDestinations() {
    assertEquals(Hottest, match("claw://hottest"))
    assertEquals(Newest, match("claw://newest"))
    assertEquals(Saved, match("claw://saved"))
    assertEquals(
      Comments("abc123", "https://lobste.rs/s/abc123/c"),
      match("claw://comments/abc123"),
    )
    assertEquals(
      Comments("abc123", "https://lobste.rs/s/abc123/c"),
      match("claw://comments/abc123?source=widget"),
    )
  }

  @Test
  fun rejectsUnrecognizedLinks() {
    assertNull(match("claw-debug://comments/abc123"))
    assertNull(match("https://comments/abc123"))
    assertNull(match("claw://unknown"))
    assertNull(match("claw://comments"))
    assertNull(match("claw://comments/"))
    assertNull(match("claw://comments/abc123/extra"))
  }
}
