package dev.msfjarvis.lobsters.data.api

import dev.msfjarvis.lobsters.model.LobstersPost

/**
 * Simple interface defining an API for lobste.rs
 */
interface LobstersApi {

  suspend fun getHottestPosts(page: Int): List<LobstersPost>

  companion object {
    const val BASE_URL = "https://lobste.rs"
  }
}
