package dev.msfjarvis.claw.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.msfjarvis.claw.api.LobstersSearchApi
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.ui.search.SearchScreen.Event
import dev.msfjarvis.claw.ui.search.SearchScreen.State
import javax.inject.Inject

class SearchPresenter
@Inject
constructor(
  val navigator: Navigator,
  val api: LobstersSearchApi,
) : Presenter<State> {
  @Composable
  override fun present(): State {
    val searchResults = produceState<List<LobstersPost>?>(null) { value = emptyList() }.value
    return when {
      searchResults == null -> State.Loading
      searchResults.isEmpty() -> State.NoResults
      else -> State.Result(searchResults) { event ->
        when (event) {
          is Event.ScrollToPage -> TODO()
          is Event.SubmitSearchQuery -> TODO()
        }
      }
    }
  }
}
