/*
 * Copyright © 2021-2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common

sealed class NetworkState {
  class Success<T>(val data: T) : NetworkState()
  class Error(val error: Throwable, val description: String) : NetworkState()
  object Loading : NetworkState()
}
