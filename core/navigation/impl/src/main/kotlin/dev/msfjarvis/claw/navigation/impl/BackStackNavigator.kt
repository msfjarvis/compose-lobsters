/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.navigation.impl

import androidx.navigation3.runtime.NavKey
import dev.msfjarvis.claw.navigation.api.Navigator
import dev.msfjarvis.claw.navigation.api.NonStackable
import dev.msfjarvis.claw.navigation.api.TopLevelDestination

/** Implements the app's established top-level reset, duplicate and back-stack rules. */
class BackStackNavigator(
  private val backStack: MutableList<NavKey>,
  private val root: NavKey,
) : Navigator {
  override fun navigate(destination: NavKey, allowStacking: Boolean) {
    if (destination is TopLevelDestination) {
      backStack.clear()
      if (destination != root) backStack.add(root)
      backStack.add(destination)
      return
    }

    if (destination is NonStackable) {
      if (allowStacking) {
        if (backStack.lastOrNull()?.let(destination::isSameStackEntry) == true) return
        backStack.add(destination)
        return
      }

      val existingEntry = backStack.firstOrNull {
        it is NonStackable && destination.isSameDestination(it)
      }
      if (existingEntry != null) backStack.remove(existingEntry)
    }
    backStack.add(destination)
  }

  override fun pop(): NavKey? {
    if (backStack.size <= 1) return null
    return backStack.removeAt(backStack.lastIndex)
  }
}
