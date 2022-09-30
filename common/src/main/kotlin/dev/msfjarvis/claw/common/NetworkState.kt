package dev.msfjarvis.claw.common

sealed class NetworkState {
  class Success<T>(val data: T) : NetworkState()
  class Error(val error: Throwable, val description: String) : NetworkState()
  object Loading : NetworkState()
}
