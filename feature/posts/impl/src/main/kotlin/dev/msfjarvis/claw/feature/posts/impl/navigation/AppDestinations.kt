/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.posts.impl.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import dev.msfjarvis.claw.android.ui.navigation.Hottest
import dev.msfjarvis.claw.android.ui.navigation.Newest
import dev.msfjarvis.claw.android.ui.navigation.Saved

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
