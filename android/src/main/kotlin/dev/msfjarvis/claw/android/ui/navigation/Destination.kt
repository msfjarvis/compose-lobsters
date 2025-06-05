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

@Parcelize @Serializable data object Hottest : NavKey, TopLevelDestination, Parcelable

@Parcelize @Serializable data object Newest : NavKey, TopLevelDestination, Parcelable

@Parcelize @Serializable data object Saved : NavKey, TopLevelDestination, Parcelable

@Parcelize @Serializable data class Comments(val postId: String) : NavKey, Parcelable

@Parcelize @Serializable data class User(val username: String) : NavKey, Parcelable

@Parcelize @Serializable data object Search : NavKey, Parcelable

@Parcelize @Serializable data object Settings : NavKey, Parcelable

@Parcelize @Serializable data object AboutLibraries : NavKey, Parcelable

enum class AppDestinations(
  val icon: ImageVector,
  val label: String,
  val navKey: NavKey,
  val selectedIcon: ImageVector,
) {
  HOTTEST(
    icon = Icons.Outlined.Whatshot,
    label = "Hottest",
    navKey = Hottest,
    selectedIcon = Icons.Filled.Whatshot,
  ),
  NEWEST(
    icon = Icons.Outlined.NewReleases,
    label = "Newest",
    navKey = Newest,
    selectedIcon = Icons.Filled.NewReleases,
  ),
  SAVED(
    icon = Icons.Outlined.FavoriteBorder,
    label = "Saved",
    navKey = Saved,
    selectedIcon = Icons.Filled.Favorite,
  ),
}
