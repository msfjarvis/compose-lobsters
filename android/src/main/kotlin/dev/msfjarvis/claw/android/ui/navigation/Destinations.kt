/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.navigation

sealed class Destinations {
  abstract val route: String

  object Hottest : Destinations() {
    override val route = "hottest"
  }

  object Newest : Destinations() {
    override val route = "newest"
  }

  object Saved : Destinations() {
    override val route = "saved"
  }

  object Comments : Destinations() {
    const val placeholder = "{postId}"
    override val route = "comments/$placeholder"
  }

  object User : Destinations() {
    const val placeholder = "{username}"
    override val route = "user/$placeholder"
  }

  object DataTransfer : Destinations() {
    override val route: String = "datatransfer"
  }

  object Search : Destinations() {
    override val route: String = "search"
  }

  object ShioriConfig : Destinations() {
    override val route: String = "shiori_config"
  }

  companion object {
    val startDestination
      get() = Hottest
  }
}
