package dev.msfjarvis.lobsters.data.api

import dev.msfjarvis.lobsters.data.api.LobstersApi.Companion.BASE_URL
import dev.msfjarvis.lobsters.model.LobstersPost
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import javax.inject.Inject

/**
 * Ktor backed implementation of [LobstersApi]
 */
class KtorLobstersApi @Inject constructor(private val client: HttpClient) : LobstersApi {
  override suspend fun getHottestPosts(page: Int): List<LobstersPost> {
    return client.get("${BASE_URL}/hottest.json?page=$page")
  }
}
