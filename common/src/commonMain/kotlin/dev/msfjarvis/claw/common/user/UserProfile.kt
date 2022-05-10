package dev.msfjarvis.claw.common.user

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.msfjarvis.claw.model.User

@Suppress("UNCHECKED_CAST")
@Composable
fun UserProfile(
  username: String,
  getProfile: suspend (username: String) -> User,
  modifier: Modifier = Modifier,
) {
}
