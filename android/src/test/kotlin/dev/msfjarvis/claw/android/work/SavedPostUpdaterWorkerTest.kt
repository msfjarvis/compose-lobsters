/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.work

import com.google.common.truth.Truth.assertThat
import dev.msfjarvis.claw.model.LobstersPostDetails
import org.junit.jupiter.api.Test

class SavedPostUpdaterWorkerTest {
  @Test
  fun `refresh replaces homepage comment anchors with a story URL`() {
    val url = savedPostRefreshUrl("sxlf4a", "https://lobste.rs#comments-sxlf4a")

    assertThat(url).isEqualTo("https://lobste.rs/s/sxlf4a/c")
    assertThat(refreshedSavedPost("sxlf4a", url, details("sxlf4a"))?.commentsUrl).isEqualTo(url)
  }

  @Test
  fun `refresh retains a valid story URL even if the detail page has a comment anchor`() {
    val url = "https://lobste.rs/s/sxlf4a/goodbye_google"

    assertThat(savedPostRefreshUrl("sxlf4a", url)).isEqualTo(url)
    assertThat(refreshedSavedPost("sxlf4a", url, details("sxlf4a"))?.commentsUrl).isEqualTo(url)
  }

  @Test
  fun `refresh rejects a different story or host and retains hyphenated slugs`() {
    assertThat(savedPostRefreshUrl("sxlf4a", "https://lobste.rs/s/other1/goodbye_google"))
      .isEqualTo("https://lobste.rs/s/sxlf4a/c")
    assertThat(savedPostRefreshUrl("sxlf4a", "https://example.org/s/sxlf4a/goodbye_google"))
      .isEqualTo("https://lobste.rs/s/sxlf4a/c")
    assertThat(savedPostRefreshUrl("sxlf4a", "https://lobste.rs/s/sxlf4a/goodbye-google"))
      .isEqualTo("https://lobste.rs/s/sxlf4a/goodbye-google")
  }

  @Test
  fun `refresh does not save homepage story details under a different short id`() {
    val url = savedPostRefreshUrl("sxlf4a", "https://lobste.rs/")

    assertThat(refreshedSavedPost("sxlf4a", url, details("other1"))).isNull()
  }

  private fun details(shortId: String) =
    LobstersPostDetails(
      shortId = shortId,
      title = "Goodbye Google",
      submitter = "classichasclass",
      commentsUrl = "https://lobste.rs#comments-$shortId",
    )
}
