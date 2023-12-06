/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common

internal sealed class NetworkState {
  class Success<T>(val data: T) : NetworkState()

  class Error(val error: Throwable, val description: String) : NetworkState()

  data object Loading : NetworkState()
}
