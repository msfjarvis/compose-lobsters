/*
 * Copyright © 2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import kotlinx.collections.immutable.ImmutableList

/**
 * Walk through the [NavDestination]'s [hierarchy] to see if it has any destination that matches the
 * route defined by [dest].
 */
fun NavDestination?.matches(dest: Destination): Boolean {
  return this?.hierarchy?.any { it.hasRoute(dest::class) } == true
}

/** Check if this [NavDestination] [matches] any of the potential navigation [destinations]. */
fun NavDestination?.any(destinations: ImmutableList<Destination>): Boolean {
  return destinations.any { this?.matches(it) == true }
}

/** Check if this [NavDestination] [matches] none of the potential navigation [destinations]. */
fun NavDestination?.none(destinations: ImmutableList<Destination>): Boolean {
  return destinations.none { this?.matches(it) == true }
}
