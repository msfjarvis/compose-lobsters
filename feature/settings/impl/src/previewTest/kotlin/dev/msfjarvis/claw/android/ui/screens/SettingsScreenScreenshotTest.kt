/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.feature.settings.impl.SettingsAppConfig
import kotlinx.datetime.LocalTime

@PreviewTest
@Preview(name = "Compact", widthDp = 360, heightDp = 800)
@Preview(name = "Medium", widthDp = 700, heightDp = 900)
@Preview(name = "Expanded", widthDp = 1000, heightDp = 900)
@Preview(name = "Short height", widthDp = 360, heightDp = 480)
@Preview(name = "Large font", widthDp = 360, heightDp = 800, fontScale = 1.5f)
@Composable
private fun SettingsScreenScreenshot() {
  LobstersTheme {
    SettingsScreen(
      openLibrariesScreen = {},
      openRepository = {},
      openTagFiltering = {},
      openLoginScreen = {},
      onLogout = {},
      isLoggedIn = true,
      username = "lobster",
      snackbarHostState = SnackbarHostState(),
      importPosts = { Result.success(Unit) },
      exportPostsAsJson = {},
      contentPadding = PaddingValues(),
      savedPostsCount = 42,
      isDailySavedPostReminderEnabled = true,
      dailySavedPostReminderTime = LocalTime(9, 0),
      onDailySavedPostReminderEnabledChange = {},
      onDailySavedPostReminderTimeChange = {},
      settingsAppConfig = SettingsAppConfig("1.0", 1) {},
    )
  }
}
