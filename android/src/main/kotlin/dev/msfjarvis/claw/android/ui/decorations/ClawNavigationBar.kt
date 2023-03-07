/*
 * Copyright © 2022-2023 Harsh Shandilya.
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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavController
import dev.msfjarvis.claw.android.ui.navigation.Destinations
import kotlinx.collections.immutable.ImmutableList

private const val AnimationDuration = 100

@Composable
fun ClawNavigationBar(
  navController: NavController,
  items: ImmutableList<NavigationItem>,
  isVisible: Boolean,
  modifier: Modifier = Modifier,
) {
  AnimatedVisibility(
    visible = isVisible,
    enter =
      slideInVertically(
        // Enters by sliding up from offset 0 to fullHeight.
        initialOffsetY = { fullHeight -> fullHeight },
        animationSpec = tween(durationMillis = AnimationDuration, easing = LinearOutSlowInEasing),
      ),
    exit =
      slideOutVertically(
        // Exits by sliding up from offset 0 to -fullHeight.
        targetOffsetY = { fullHeight -> fullHeight },
        animationSpec = tween(durationMillis = AnimationDuration, easing = FastOutLinearInEasing),
      ),
    modifier = Modifier,
  ) {
    NavigationBar(modifier = modifier) {
      items.forEach { navItem ->
        val isCurrentDestination = navController.currentDestination?.route == navItem.route
        NavigationBarItem(
          icon = {
            Crossfade(isCurrentDestination) {
              Icon(
                painter = if (it) navItem.selectedIcon else navItem.icon,
                contentDescription = navItem.label.uppercase()
              )
            }
          },
          label = { Text(text = navItem.label) },
          selected = isCurrentDestination,
          onClick = {
            if (isCurrentDestination) {
              navItem.listStateResetCallback()
              return@NavigationBarItem
            }
            navController.graph.startDestinationRoute?.let { startDestination ->
              navController.popBackStack(startDestination, false)
            }
            if (navItem.route != Destinations.startDestination.route) {
              navController.navigate(navItem.route)
            }
          },
          modifier = Modifier.testTag(navItem.label.uppercase()),
        )
      }
    }
  }
}

class NavigationItem(
  val label: String,
  val route: String,
  val icon: Painter,
  val selectedIcon: Painter,
  val listStateResetCallback: () -> Unit,
)
