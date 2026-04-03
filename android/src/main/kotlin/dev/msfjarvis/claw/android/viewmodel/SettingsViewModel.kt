/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.msfjarvis.claw.core.network.SessionCookieStore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@Inject
@ViewModelKey
@ContributesIntoMap(scope = AppScope::class, binding = binding<ViewModel>())
class SettingsViewModel(private val sessionCookieStore: SessionCookieStore) : ViewModel() {

  val isLoggedIn =
    sessionCookieStore
      .isLoggedIn()
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false,
      )

  fun logout() {
    sessionCookieStore.clear()
  }

  fun saveCookie(cookie: String) {
    sessionCookieStore.set(cookie)
  }
}
