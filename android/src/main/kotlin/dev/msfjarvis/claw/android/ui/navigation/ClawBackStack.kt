/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavKey
import io.github.aakira.napier.Napier

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
 * structure, many APIs in this class inverse of identically named functions on [List].
 */
class ClawBackStack<T : NavKey>(startRoute: T) {
  interface TopLevelRoute

  val backStack = mutableStateListOf(startRoute)

  /** Pushes a new destination onto the stack. */
  fun add(route: T) {
    logCurrentState("add")
    backStack.add(route)
  }

  /** Checks if the "top" item in the back stack is an instance of [TopLevelRoute]. */
  fun isOnTopLevelRoute(): Boolean {
    logCurrentState("hasTopLevelDestination")
    val top = firstOrNull()
    return (top != null && top is TopLevelRoute)
  }

  fun firstOrNull(): T? {
    logCurrentState("firstOrNull")
    return backStack.lastOrNull()
  }

  fun lastOrNull(): T? {
    logCurrentState("lastOrNull")
    return backStack.firstOrNull()
  }

  fun removeLastOrNull(): T? {
    logCurrentState("removeLastOrNull")
    return backStack.removeLastOrNull()
  }

  // TODO(msfjarvis): Remove before shipping
  private fun logCurrentState(methodName: String) {
    val backStack = this.backStack
    Napier.d(tag = LOG_TAG) {
      buildString {
        appendLine("State of ClawBackStack(${this@ClawBackStack})")
        appendLine("Caller: $methodName")
        appendLine("Top: ${backStack.firstOrNull()}")
        appendLine("Bottom: ${backStack.lastOrNull()}")
        appendLine("Current entries: ${backStack.joinToString(", ")}")
      }
    }
  }

  private companion object {
    private const val LOG_TAG = "ClawBackStack"
  }
}
