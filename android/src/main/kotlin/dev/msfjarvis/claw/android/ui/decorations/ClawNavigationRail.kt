/*
 * Copyright © 2022-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.decorations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.msfjarvis.claw.android.ui.matches
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ClawNavigationRail(
  navController: NavController,
  items: ImmutableList<NavigationItem>,
  isVisible: Boolean,
  modifier: Modifier = Modifier,
) {
  AnimatedVisibility(
    visible = isVisible,
    enter =
      slideInHorizontally(
        // Enters by sliding in from offset -fullWidth to 0.
        initialOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(durationMillis = AnimationDuration, easing = LinearOutSlowInEasing),
      ),
    exit =
      slideOutHorizontally(
        // Exits by sliding out from offset 0 to -fullWidth.
        targetOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(durationMillis = AnimationDuration, easing = FastOutLinearInEasing),
      ),
    modifier = Modifier,
  ) {
    NavigationRail(modifier = modifier) {
      val navBackStackEntry = navController.currentBackStackEntryAsState().value
      val currentDestination = navBackStackEntry?.destination
      Spacer(Modifier.weight(1f))
      items.forEach { navItem ->
        val isSelected = currentDestination.matches(navItem.destination)
        NavigationRailItem(
          icon = {
            Crossfade(isSelected, label = "nav-label") {
              Icon(
                imageVector = if (it) navItem.selectedIcon else navItem.icon,
                contentDescription = navItem.label.replaceFirstChar(Char::uppercase),
              )
            }
          },
          label = { Text(text = navItem.label) },
          selected = isSelected,
          onClick = {
            if (isSelected) {
              navItem.listStateResetCallback()
            } else {
              navController.navigate(navItem.destination) {
                popUpTo(navController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
              }
            }
          },
          modifier = Modifier.testTag(navItem.label.uppercase()),
        )
      }
      Spacer(Modifier.weight(1f))
    }
  }
}
