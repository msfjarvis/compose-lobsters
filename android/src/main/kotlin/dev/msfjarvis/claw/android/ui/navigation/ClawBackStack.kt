/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavKey

/**
 * Naive implementation of a simple navigation back stack, backed by a
 * [androidx.compose.runtime.snapshots.SnapshotStateList].
 *
 * Using a [java.util.Stack] would've made the API ridiculously simpler, but we would lose all the
 * cool Compose benefits.
 *
 * Stacks function in a completely opposite order of lists, which means when you call
 * first/firstOrNull on a stack, you expect to receive the item you added *last*, since stacks add
 * to the front while lists add behind. To counter these expectations with the actual backing data
 * structure, many APIs in this class are the inverse of identically named functions on [List].
 */
class ClawBackStack(private val initialDestination: NavKey) {
  /**
   * Marker interface for destinations that occupy the "top" level of the back stack.
   *
   * This is used by [dev.msfjarvis.claw.android.ui.navigation.ClawBackStack.add] to ensure that
   * duplicate instances of these destinations do not end up in the back stack.
   */
  interface TopLevelDestination

  val backStack = mutableStateListOf(initialDestination)

  /**
   * Pushes a new destination onto the stack.
   *
   * Top level destinations are specially handled to ensure that the distance between the incoming
   * [destination] and the [initialDestination] cannot have anything in between. This prevents users
   * from getting stuck in a frustratingly long stack of top level destinations that are so easily
   * accessible that they have no reason to be on the stack.
   */
  fun add(destination: NavKey) {
    if (destination is TopLevelDestination) {
      backStack.clear()
      if (destination != initialDestination) {
        backStack.add(initialDestination)
      }
    }
    backStack.add(destination)
  }

  /** Checks if the "top" item in the back stack is an instance of [TopLevelDestination]. */
  fun isOnTopLevelDestination(): Boolean {
    val top = firstOrNull()
    return top != null && top is TopLevelDestination
  }

  fun firstOrNull() = backStack.lastOrNull()

  fun removeLastOrNull() = backStack.removeLastOrNull()
}
