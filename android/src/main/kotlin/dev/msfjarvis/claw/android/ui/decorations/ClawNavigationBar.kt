/*
 * Copyright © Harsh Shandilya.
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.navigation3.runtime.NavKey
import dev.msfjarvis.claw.android.ui.navigation.AppDestinations
import kotlinx.collections.immutable.ImmutableList

const val AnimationDuration = 100

@Composable
fun ClawNavigationBar(
  items: ImmutableList<NavigationItem>,
  currentNavKey: NavKey?,
  navigateTo: (NavKey) -> Unit,
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
    label = "",
    content = {
      NavigationBar(modifier = modifier) {
        items.forEach { navItem ->
          val isSelected = currentNavKey == navItem.navKey
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
                navigateTo(navItem.navKey)
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
  val navKey: NavKey,
  val selectedIcon: ImageVector,
  val listStateResetCallback: () -> Unit,
) {
  constructor(
    destination: AppDestinations,
    listStateResetCallback: () -> Unit,
  ) : this(
    destination.icon,
    destination.label,
    destination.navKey,
    destination.selectedIcon,
    listStateResetCallback,
  ) {}
}
