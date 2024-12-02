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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeChild
import dev.msfjarvis.claw.android.ui.navigation.AppDestinations
import dev.msfjarvis.claw.android.ui.navigation.Destination
import dev.msfjarvis.claw.android.ui.navigation.matches
import dev.msfjarvis.claw.common.ui.FloatingNavigationBar
import kotlinx.collections.immutable.ImmutableList

const val AnimationDuration = 100

@Composable
fun ClawNavigationBar(
  navController: NavController,
  items: ImmutableList<NavigationItem>,
  isVisible: Boolean,
  hazeState: HazeState,
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
    label = "",
    content = {
      FloatingNavigationBar(
        tonalElevation = 16.dp,
        shape = MaterialTheme.shapes.extraLarge,
        modifier =
          modifier
            .padding(horizontal = 16.dp)
            .navigationBarsPadding()
            .clip(MaterialTheme.shapes.extraLarge)
            .hazeChild(
              hazeState,
              style =
                HazeStyle(
                  backgroundColor = MaterialTheme.colorScheme.surface,
                  tints = emptyList(),
                  blurRadius = 24.dp,
                  noiseFactor = 0f,
                ),
            ),
        containerColor =
          if (HazeDefaults.blurEnabled()) Color.Transparent else MaterialTheme.colorScheme.surface,
      ) {
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
    },
  )
}

class NavigationItem
private constructor(
  val icon: ImageVector,
  val label: String,
  val destination: Destination,
  val selectedIcon: ImageVector,
  val listStateResetCallback: () -> Unit,
) {
  constructor(
    destination: AppDestinations,
    listStateResetCallback: () -> Unit,
  ) : this(
    destination.icon,
    destination.label,
    destination.destination,
    destination.selectedIcon,
    listStateResetCallback,
  ) {}
}
