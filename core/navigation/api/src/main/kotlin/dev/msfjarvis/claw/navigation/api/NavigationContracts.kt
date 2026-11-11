/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.navigation.api

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

/** Marks a destination that resets navigation to the application's root destination. */
interface TopLevelDestination

/** Marks a destination whose existing instance is replaced during ordinary navigation. */
interface NonStackable {
  fun isSameDestination(other: NavKey): Boolean =
    other is NonStackable && this::class == other::class

  fun isSameStackEntry(other: NavKey): Boolean = this == other
}

/** Navigation operations exposed to screens without exposing the mutable back stack. */
interface Navigator {
  fun navigate(destination: NavKey, allowStacking: Boolean = false)

  fun pop(): NavKey?
}

/** Converts an external URI into an app-owned navigation key, if the URI is recognized. */
fun interface DeepLinkMatcher {
  fun match(uri: String): NavKey?
}

/** Adds app-owned destination entries to the generic navigation host. */
fun interface NavigationEntryContributor {
  fun EntryProviderScope<NavKey>.addEntries()
}

/** Returns the first destination recognized by the supplied app-owned matchers. */
fun matchDeepLink(uri: String, matchers: Iterable<DeepLinkMatcher>): NavKey? =
  matchers.firstNotNullOfOrNull {
    it.match(uri)
  }
