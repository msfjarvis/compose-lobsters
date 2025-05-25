/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.navigation

import android.os.Parcelable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import dev.msfjarvis.claw.android.ui.navigation.ClawBackStack.TopLevelDestination
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

sealed interface Destination : Parcelable, NavKey

@Parcelize @Serializable data object Hottest : Destination, TopLevelDestination

@Parcelize @Serializable data object Newest : Destination, TopLevelDestination

@Parcelize @Serializable data object Saved : Destination, TopLevelDestination

@Parcelize @Serializable data class Comments(val postId: String) : Destination

@Parcelize @Serializable data class User(val username: String) : Destination

@Parcelize @Serializable data object Search : Destination

@Parcelize @Serializable data object Settings : Destination

@Parcelize @Serializable data object AboutLibraries : Destination

enum class AppDestinations(
  val icon: ImageVector,
  val label: String,
  val destination: Destination,
  val selectedIcon: ImageVector,
) {
  HOTTEST(
    icon = Icons.Outlined.Whatshot,
    label = "Hottest",
    destination = Hottest,
    selectedIcon = Icons.Filled.Whatshot,
  ),
  NEWEST(
    icon = Icons.Outlined.NewReleases,
    label = "Newest",
    destination = Newest,
    selectedIcon = Icons.Filled.NewReleases,
  ),
  SAVED(
    icon = Icons.Outlined.FavoriteBorder,
    label = "Saved",
    destination = Saved,
    selectedIcon = Icons.Filled.Favorite,
  ),
}
