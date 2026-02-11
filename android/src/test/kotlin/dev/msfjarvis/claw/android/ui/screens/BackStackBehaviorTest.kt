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
    val backStack = makeBackStack(Hottest, Comments("abc123"))

    navigateTo(backStack, Comments("def456"))

    assertThat(backStack).containsExactly(Hottest, Comments("def456")).inOrder()
  }

  @Test
  fun `NonStackable destinations stack when allowStacking = true`() {
    val backStack = makeBackStack(Hottest, Comments("abc123"))

    navigateTo(backStack, Comments("def456"), allowStacking = true)

    assertThat(backStack).containsExactly(Hottest, Comments("abc123"), Comments("def456")).inOrder()
  }

  @Test
  fun `Same destination cannot be stacked on itself`() {
    val backStack = makeBackStack(Hottest, Comments("abc123"))

    navigateTo(backStack, Comments("abc123"), allowStacking = true)

    assertThat(backStack).containsExactly(Hottest, Comments("abc123")).inOrder()
  }

  @Test
  fun `Same post replaces when target comment differs`() {
    val backStack = makeBackStack(Hottest, Comments("abc123"))

    navigateTo(backStack, Comments("abc123", "def456"), allowStacking = true)

    assertThat(backStack)
      .containsExactly(Hottest, Comments("abc123", "def456"))
      .inOrder()
  }

  @Test
  fun `Same destination can be stacked with a gap in between`() {
    val backStack = makeBackStack(Hottest, Comments("abc123"), Comments("def456"))

    navigateTo(backStack, Comments("abc123"), allowStacking = true)

    assertThat(backStack)
      .containsExactly(Hottest, Comments("abc123"), Comments("def456"), Comments("abc123"))
      .inOrder()
  }
}
