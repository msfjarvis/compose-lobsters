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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.msfjarvis.claw.android.ui.navigation.Destination
import dev.msfjarvis.claw.android.ui.navigation.matches
import kotlinx.collections.immutable.ImmutableList

const val AnimationDuration = 100

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClawNavigationBar(
  navController: NavController,
  items: ImmutableList<NavigationItem>,
  isVisible: Boolean,
  scrollBehavior: BottomAppBarScrollBehavior,
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
    BottomAppBar(modifier = modifier, scrollBehavior = scrollBehavior) {
      val navBackStackEntry = navController.currentBackStackEntryAsState().value
      val currentDestination = navBackStackEntry?.destination
      items.forEach { navItem ->
        val isSelected = currentDestination.matches(navItem.destination)
        NavigationBarItem(
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
    }
  }
}

class NavigationItem(
  val label: String,
  val destination: Destination,
  val icon: ImageVector,
  val selectedIcon: ImageVector,
  val listStateResetCallback: () -> Unit,
)
