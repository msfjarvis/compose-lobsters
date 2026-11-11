/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.settings.impl.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.msfjarvis.claw.android.ui.navigation.TagFiltering
import dev.msfjarvis.claw.android.ui.screens.SettingsScreen
import dev.msfjarvis.claw.feature.settings.impl.SettingsAppConfig
import dev.msfjarvis.claw.navigation.api.NavigationEntryContributor
import java.io.InputStream
import java.io.OutputStream
import kotlinx.datetime.LocalTime

/**
 * Adds settings-owned destinations; app-only library metadata is supplied by the composition root.
 */
fun settingsNavigationEntryContributor(
  metadata: Map<String, Any>,
  contentPadding: PaddingValues,
  openLibrariesScreen: () -> Unit,
  openRepository: () -> Unit,
  openTagFiltering: (TagFiltering) -> Unit,
  openLoginScreen: () -> Unit,
  onLogout: () -> Unit,
  isLoggedIn: Boolean,
  username: String?,
  snackbarHostState: SnackbarHostState,
  importPosts: suspend (InputStream) -> Result<Unit>,
  exportPostsAsJson: suspend (OutputStream) -> Unit,
  savedPostsCount: Long,
  isDailySavedPostReminderEnabled: Boolean,
  dailySavedPostReminderTime: LocalTime,
  onDailySavedPostReminderEnabledChange: (Boolean) -> Unit,
  onDailySavedPostReminderTimeChange: (LocalTime) -> Unit,
  settingsAppConfig: SettingsAppConfig,
  librariesContent: @Composable (PaddingValues, Modifier) -> Unit,
): NavigationEntryContributor = NavigationEntryContributor {
  entry<dev.msfjarvis.claw.android.ui.navigation.Settings>(metadata = metadata) {
    SettingsScreen(
      openLibrariesScreen = openLibrariesScreen,
      openRepository = openRepository,
      openTagFiltering = openTagFiltering,
      openLoginScreen = openLoginScreen,
      onLogout = onLogout,
      isLoggedIn = isLoggedIn,
      username = username,
      snackbarHostState = snackbarHostState,
      importPosts = importPosts,
      exportPostsAsJson = exportPostsAsJson,
      contentPadding = contentPadding,
      savedPostsCount = savedPostsCount,
      isDailySavedPostReminderEnabled = isDailySavedPostReminderEnabled,
      dailySavedPostReminderTime = dailySavedPostReminderTime,
      onDailySavedPostReminderEnabledChange = onDailySavedPostReminderEnabledChange,
      onDailySavedPostReminderTimeChange = onDailySavedPostReminderTimeChange,
      settingsAppConfig = settingsAppConfig,
      modifier = Modifier.fillMaxSize(),
    )
  }
  entry<dev.msfjarvis.claw.android.ui.navigation.AboutLibraries>(metadata = metadata) {
    librariesContent(contentPadding, Modifier.fillMaxSize())
  }
}
