/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.profile.impl.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation3.runtime.NavKey
import dev.msfjarvis.claw.android.ui.navigation.User
import dev.msfjarvis.claw.common.user.UserProfile
import dev.msfjarvis.claw.navigation.api.NavigationEntryContributor

fun profileNavigationEntryContributor(
  contentPadding: PaddingValues,
  metadata: Map<String, Any>,
  navigate: (NavKey, Boolean) -> Unit,
): NavigationEntryContributor = NavigationEntryContributor {
  entry<User>(metadata = metadata) { destination ->
    UserProfile(
      username = destination.username,
      contentPadding = contentPadding,
      openUserProfile = { navigate(User(it), false) },
    )
  }
}
