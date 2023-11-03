/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.shiori

import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import javax.inject.Inject

class ShioriSettings
@Inject
constructor(
  private val sharedPreferences: EncryptedSharedPreferences,
) {

  fun configure(url: String?, username: String?, password: String?) {
    sharedPreferences.edit {
      putString(SHIORI_USERNAME, username)
      putString(SHIORI_PASSWORD, password)
      putString(SHIORI_URL, url)
    }
  }

  fun getUrl(): String? {
    return sharedPreferences.getString(SHIORI_URL, null)
  }

  fun getUsername(): String? {
    return sharedPreferences.getString(SHIORI_USERNAME, null)
  }

  fun getPassword(): String? {
    return sharedPreferences.getString(SHIORI_PASSWORD, null)
  }

  private companion object {
    private const val SHIORI_USERNAME = "shiori_username"
    private const val SHIORI_PASSWORD = "shiori_password"
    private const val SHIORI_URL = "shiori_url"
  }
}
