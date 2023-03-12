/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import javax.inject.Inject

class LoginSettings
@Inject
constructor(
  val dataStore: DataStore<Preferences>,
) {
  companion object {
    val CSRF_TOKEN = stringPreferencesKey("csrf_token")
    val LOGIN_COOKIE = stringPreferencesKey("login_cookie")
  }
}
