package dev.msfjarvis.claw.ui.search

import android.os.Parcelable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import dev.msfjarvis.claw.model.LobstersPost
import kotlinx.parcelize.Parcelize

@Parcelize
data object SearchScreen : Parcelable {
  sealed interface State : CircuitUiState {
    data object Loading : State
    data object NoResults : State
    data class Result(
      val list: List<LobstersPost>,
      val eventSink: (Event) -> Unit,
    ): State
  }

  sealed interface Event : CircuitUiEvent {
    data class SubmitSearchQuery(val query: String): Event
    data class ScrollToPage(val nextPageNumber: Int): Event
  }
}
