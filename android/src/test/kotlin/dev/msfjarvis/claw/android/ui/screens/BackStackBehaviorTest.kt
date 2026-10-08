/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.screens

import androidx.navigation3.runtime.NavKey
import com.google.common.truth.Truth.assertThat
import dev.msfjarvis.claw.android.ui.navigation.Comments
import dev.msfjarvis.claw.android.ui.navigation.Hottest
import dev.msfjarvis.claw.android.ui.navigation.Newest
import dev.msfjarvis.claw.android.ui.navigation.Saved
import org.junit.jupiter.api.Test

class BackStackBehaviorTest {

  private fun makeBackStack(vararg keys: NavKey) = mutableListOf(*keys)

  @Test
  fun `navigating from between top level destinations prevents stacking`() {
    val backStack = makeBackStack(Hottest)

    navigateTo(backStack, Newest)

    assertThat(backStack).containsExactly(Hottest, Newest).inOrder()

    navigateTo(backStack, Saved)

    assertThat(backStack).containsExactly(Hottest, Saved).inOrder()
  }

  @Test
  fun `NonStackable destinations do not stack by default`() {
    val backStack = makeBackStack(Hottest, Comments("abc123", "https://lobste.rs/s/abc123/c"))

    navigateTo(backStack, Comments("def456", "https://lobste.rs/s/def456/c"))

    assertThat(backStack)
      .containsExactly(Hottest, Comments("def456", "https://lobste.rs/s/def456/c"))
      .inOrder()
  }

  @Test
  fun `NonStackable destinations stack when allowStacking = true`() {
    val backStack = makeBackStack(Hottest, Comments("abc123", "https://lobste.rs/s/abc123/c"))

    navigateTo(backStack, Comments("def456", "https://lobste.rs/s/def456/c"), allowStacking = true)

    assertThat(backStack)
      .containsExactly(
        Hottest,
        Comments("abc123", "https://lobste.rs/s/abc123/c"),
        Comments("def456", "https://lobste.rs/s/def456/c"),
      )
      .inOrder()
  }

  @Test
  fun `Same destination cannot be stacked on itself`() {
    val backStack = makeBackStack(Hottest, Comments("abc123", "https://lobste.rs/s/abc123/c"))

    navigateTo(backStack, Comments("abc123", "https://lobste.rs/s/abc123/c"), allowStacking = true)

    assertThat(backStack)
      .containsExactly(Hottest, Comments("abc123", "https://lobste.rs/s/abc123/c"))
      .inOrder()
  }

  @Test
  fun `Same destination can be stacked with a gap in between`() {
    val backStack =
      makeBackStack(
        Hottest,
        Comments("abc123", "https://lobste.rs/s/abc123/c"),
        Comments("def456", "https://lobste.rs/s/def456/c"),
      )

    navigateTo(backStack, Comments("abc123", "https://lobste.rs/s/abc123/c"), allowStacking = true)

    assertThat(backStack)
      .containsExactly(
        Hottest,
        Comments("abc123", "https://lobste.rs/s/abc123/c"),
        Comments("def456", "https://lobste.rs/s/def456/c"),
        Comments("abc123", "https://lobste.rs/s/abc123/c"),
      )
      .inOrder()
  }

  @Test
  fun `back dismisses active top level search`() {
    assertThat(
        shouldDismissSearchOnBack(isSearchActive = true, isCurrentDestinationTopLevel = true)
      )
      .isTrue()
  }

  @Test
  fun `back does not intercept navigation when search is inactive`() {
    assertThat(
        shouldDismissSearchOnBack(isSearchActive = false, isCurrentDestinationTopLevel = true)
      )
      .isFalse()
  }

  @Test
  fun `back does not intercept navigation from a non top level destination`() {
    assertThat(
        shouldDismissSearchOnBack(isSearchActive = true, isCurrentDestinationTopLevel = false)
      )
      .isFalse()
  }
}
