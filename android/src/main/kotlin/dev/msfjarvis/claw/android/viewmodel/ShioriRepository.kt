/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import com.slack.eithernet.ApiResult
import dev.msfjarvis.claw.android.shiori.ShioriSettings
import dev.msfjarvis.claw.api.ShioriApi
import dev.msfjarvis.claw.database.local.SavedPost
import dev.msfjarvis.claw.model.shiori.AuthRequest
import dev.msfjarvis.claw.model.shiori.AuthResponse
import dev.msfjarvis.claw.model.shiori.BookmarkRequest
import dev.msfjarvis.claw.model.shiori.Tag
import io.github.aakira.napier.Napier
import javax.inject.Inject

class ShioriRepository
@Inject
constructor(
  private val settings: ShioriSettings,
  private val api: ShioriApi,
) {
  private var cachedCredentials: AuthResponse? = null

  fun getUrl() = settings.getUrl() ?: ""

  fun getUsername() = settings.getUsername() ?: ""

  fun getPassword() = settings.getPassword() ?: ""

  fun configure(url: String?, username: String?, password: String?) {
    settings.configure(url, username, password)
  }

  private suspend fun tryLogin(): Boolean {
    if (cachedCredentials != null) return true
    if (getUrl().isEmpty() || getUsername().isEmpty() || getPassword().isEmpty()) {
      Napier.d { "Cannot login due to missing parameters" }
      return false
    }
    return when (val result = api.login(AuthRequest(getUsername(), getPassword()))) {
      is ApiResult.Failure.ApiFailure -> {
        Napier.e("Failed to login: ${result.error}")
        false
      }
      is ApiResult.Failure.HttpFailure -> {
        Napier.e("Failed to login: ${result.error}")
        false
      }
      is ApiResult.Failure.NetworkFailure -> {
        Napier.e("Failed to login: ${result.error}")
        false
      }
      is ApiResult.Failure.UnknownFailure -> {
        Napier.e("Failed to login: ${result.error}")
        false
      }
      is ApiResult.Success -> {
        Napier.d { "Successfully logged in" }
        cachedCredentials = result.value
        true
      }
    }
  }

  suspend fun savePost(savedPost: SavedPost) {
    if (!tryLogin()) return
    api.addBookmark(
      cachedCredentials!!.session,
      BookmarkRequest(
        url = savedPost.url,
        createArchive = true,
        public = 1,
        tags = savedPost.tags.map(::Tag),
        title = savedPost.title,
        excerpt = ""
      )
    )
  }
}
