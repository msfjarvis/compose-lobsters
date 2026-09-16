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
  fun `back closes active top level search before popping navigation`() {
    val result = handleTopLevelBack(isSearchActive = true, isCurrentDestinationTopLevel = true)

    assertThat(result).isEqualTo(TopLevelBackAction.DismissSearch)
  }

  @Test
  fun `back pops navigation when search is inactive`() {
    val result = handleTopLevelBack(isSearchActive = false, isCurrentDestinationTopLevel = true)

    assertThat(result).isEqualTo(TopLevelBackAction.PopNavigation)
  }

  @Test
  fun `back pops navigation when hidden search state exists on non top level destination`() {
    val result = handleTopLevelBack(isSearchActive = true, isCurrentDestinationTopLevel = false)

    assertThat(result).isEqualTo(TopLevelBackAction.PopNavigation)
  }
}
