package dev.msfjarvis.claw.android.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import javax.inject.Inject

class LoginSettings @Inject constructor(
  val dataStore: DataStore<Preferences>,
) {
  companion object  {
    val CSRF_TOKEN = stringPreferencesKey("csrf_token")
    val LOGIN_COOKIE = stringPreferencesKey("login_cookie")
  }
}
