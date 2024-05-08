/*
 * Copyright © 2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.navigation

sealed class Destinations {
  abstract val route: String

  data object Hottest : Destinations() {
    override val route = "hottest"
  }

  data object Newest : Destinations() {
    override val route = "newest"
  }

  data object Saved : Destinations() {
    override val route = "saved"
  }

  data object Comments : Destinations() {
    const val PLACEHOLDER = "{postId}"
    override val route = "comments/$PLACEHOLDER"
  }

  data object User : Destinations() {
    const val PLACEHOLDER = "{username}"
    override val route = "user/$PLACEHOLDER"
  }

  data object Search : Destinations() {
    override val route = "search"
  }

  data object Settings : Destinations() {
    override val route = "settings"
  }

  data object AboutLibraries : Destinations() {
    override val route = "about_libraries"
  }

  companion object {
    val startDestination
      get() = Hottest
  }
}
