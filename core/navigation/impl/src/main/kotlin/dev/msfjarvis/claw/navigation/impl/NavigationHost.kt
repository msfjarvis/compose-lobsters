/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.navigation.impl

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.ui.NavDisplay
import dev.msfjarvis.claw.common.ui.LocalPostSharedTransitionScope
import dev.msfjarvis.claw.navigation.api.NavigationEntryContributor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/** Hosts app-supplied destinations without depending on any feature implementation. */
@Composable
fun NavigationHost(
  backStack: ImmutableList<NavKey>,
  contributors: ImmutableList<NavigationEntryContributor>,
  onBack: () -> Unit,
  popTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform,
  predictivePopTransitionSpec:
    AnimatedContentTransitionScope<Scene<NavKey>>.(Int) -> ContentTransform,
  modifier: Modifier = Modifier,
  entryDecorators: ImmutableList<NavEntryDecorator<NavKey>> = persistentListOf(),
  sceneStrategies: ImmutableList<SceneStrategy<NavKey>> = persistentListOf(),
) {
  SharedTransitionLayout {
    CompositionLocalProvider(LocalPostSharedTransitionScope provides this) {
      NavDisplay(
        backStack = backStack,
        onBack = onBack,
        modifier = modifier,
        entryDecorators = entryDecorators,
        sceneStrategies = sceneStrategies,
        popTransitionSpec = popTransitionSpec,
        predictivePopTransitionSpec = predictivePopTransitionSpec,
        entryProvider =
          entryProvider {
            contributors.forEach { contributor ->
              with(contributor) { addEntries() }
            }
          },
      )
    }
  }
}
