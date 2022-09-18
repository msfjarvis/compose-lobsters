package dev.msfjarvis.claw.common.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.msfjarvis.claw.common.NetworkState
import dev.msfjarvis.claw.common.NetworkState.Error
import dev.msfjarvis.claw.common.NetworkState.Loading
import dev.msfjarvis.claw.common.NetworkState.Success
import dev.msfjarvis.claw.common.res.ClawIcons
import dev.msfjarvis.claw.common.ui.NetworkError
import dev.msfjarvis.claw.common.ui.NetworkImage
import dev.msfjarvis.claw.common.ui.ProgressBar
import dev.msfjarvis.claw.common.ui.ThemedRichText
import dev.msfjarvis.claw.model.User

@Suppress("UNCHECKED_CAST")
@Composable
fun UserProfile(
  username: String,
  getProfile: suspend (username: String) -> User,
  modifier: Modifier = Modifier,
) {
  val user by
    produceState<NetworkState>(Loading) {
      runCatching { getProfile(username) }
        .fold(
          onSuccess = { profile -> value = Success(profile) },
          onFailure = { value = Error("Failed to load profile for $username") }
        )
    }
  when (user) {
    is Success<*> -> {
      UserProfileInternal((user as Success<User>).data)
    }
    is Error -> {
      NetworkError((user as Error).message)
    }
    Loading -> ProgressBar(modifier)
  }
}

@Composable
private fun UserProfileInternal(
  user: User,
  modifier: Modifier = Modifier,
) {
  Surface(modifier = modifier) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
      NetworkImage(
        url = "https://lobste.rs/${user.avatarUrl}",
        placeholder = ClawIcons.Account,
        contentDescription = "Avatar of ${user.username}",
        modifier = Modifier.requiredSize(120.dp).clip(CircleShape),
      )
      Text(
        text = user.username,
        style = MaterialTheme.typography.displaySmall,
      )
      ThemedRichText(
        text = user.about,
      )
      ThemedRichText(
        text = "Invited by [${user.invitedBy}](https://lobste.rs/u/${user.invitedBy})",
      )
    }
  }
}
