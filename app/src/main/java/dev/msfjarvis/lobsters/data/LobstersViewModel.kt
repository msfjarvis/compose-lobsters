package dev.msfjarvis.lobsters.data

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.msfjarvis.lobsters.api.LobstersApi
import dev.msfjarvis.lobsters.model.LobstersPost
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


@ExperimentalCoroutinesApi
class LobstersViewModel @ViewModelInject constructor(
  private val lobstersApi: LobstersApi,
) : ViewModel() {
  private var apiPage = 1
  private val _posts = MutableStateFlow<List<LobstersPost>>(emptyList())
  val posts: StateFlow<List<LobstersPost>> get() = _posts

  init {
    getMorePosts()
  }

  fun getMorePosts() {
    viewModelScope.launch {
      _posts.value += lobstersApi.getHottestPosts(apiPage)
      apiPage += 1
    }
  }
}
