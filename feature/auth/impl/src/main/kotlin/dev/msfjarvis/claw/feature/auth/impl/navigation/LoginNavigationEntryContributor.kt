/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.auth.impl.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.msfjarvis.claw.android.ui.navigation.Login
import dev.msfjarvis.claw.android.ui.screens.LoginScreen
import dev.msfjarvis.claw.navigation.api.NavigationEntryContributor

/** Contributes the auth-owned login destination to the application's navigation host. */
fun loginNavigationEntryContributor(
  onLoginSuccess: (String, String) -> Unit,
  popBackStack: () -> Unit,
  metadata: Map<String, Any>,
): NavigationEntryContributor = NavigationEntryContributor {
  entry<Login>(metadata = metadata) {
    LoginScreen(
      onLoginSuccess = onLoginSuccess,
      popBackStack = popBackStack,
      modifier = Modifier.fillMaxSize(),
    )
  }
}

/** Adds the auth entry to the current host's feature-contributed entries. */
fun EntryProviderScope<NavKey>.addLoginNavigationEntry(
  onLoginSuccess: (String, String) -> Unit,
  popBackStack: () -> Unit,
  metadata: Map<String, Any>,
) {
  with(loginNavigationEntryContributor(onLoginSuccess, popBackStack, metadata)) { addEntries() }
}
