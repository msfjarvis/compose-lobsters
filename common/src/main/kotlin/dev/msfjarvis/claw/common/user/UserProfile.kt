/*
 * Copyright © 2022-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.fold
import dev.msfjarvis.claw.common.NetworkState
import dev.msfjarvis.claw.common.NetworkState.Error
import dev.msfjarvis.claw.common.NetworkState.Loading
import dev.msfjarvis.claw.common.NetworkState.Success
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
  openUserProfile: (String) -> Unit,
  contentPadding: PaddingValues,
  modifier: Modifier = Modifier,
) {
  val user by
    produceState<NetworkState>(Loading) {
      runSuspendCatching { getProfile(username) }
        .fold(
          success = { profile -> value = Success(profile) },
          failure = {
            value = Error(error = it, description = "Failed to load profile for $username")
          },
        )
    }
  when (user) {
    is Success<*> -> {
      UserProfileInternal(
        user = (user as Success<User>).data,
        openUserProfile = openUserProfile,
        modifier = modifier,
      )
    }
    is Error -> {
      val error = user as Error
      Box(modifier = Modifier.padding(contentPadding).fillMaxSize()) {
        NetworkError(
          label = error.description,
          error = error.error,
          modifier = Modifier.align(Alignment.Center),
        )
      }
    }
    Loading -> {
      Box(modifier = Modifier.fillMaxSize()) {
        ProgressBar(modifier = Modifier.align(Alignment.Center))
      }
    }
  }
}

@Composable
private fun UserProfileInternal(
  user: User,
  openUserProfile: (String) -> Unit,
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
        placeholder = Icons.Filled.AccountCircle,
        contentDescription = "Avatar of ${user.username}",
        modifier = Modifier.requiredSize(120.dp).clip(CircleShape),
      )
      Text(text = user.username, style = MaterialTheme.typography.displaySmall)
      ThemedRichText(text = user.about)
      user.invitedBy?.let { invitedBy ->
        Text(
          text =
            buildAnnotatedString {
              append("Invited by ")
              pushLink(
                LinkAnnotation.Clickable(
                  tag = "username",
                  linkInteractionListener = { openUserProfile(invitedBy) },
                )
              )
              append(invitedBy)
            }
        )
      }
    }
  }
}
