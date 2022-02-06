package dev.msfjarvis.claw.common

sealed class NetworkState {
  class Success<T>(val data: T) : NetworkState()
  class Error(val message: String) : NetworkState()
  object Loading : NetworkState()
}
