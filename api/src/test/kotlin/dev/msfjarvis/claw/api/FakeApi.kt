package dev.msfjarvis.claw.api

import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.model.LobstersPostDetails
import dev.msfjarvis.claw.util.TestUtils.getJson
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import retrofit2.mock.BehaviorDelegate

class FakeApi(private val delegate: BehaviorDelegate<LobstersApi>) : LobstersApi {
  private val json = Json { ignoreUnknownKeys = true }
  private val hottest: List<LobstersPost> = json.decodeFromString(getJson("hottest.json"))
  private val postDetails: LobstersPostDetails =
    json.decodeFromString(getJson("post_details_d9ucpe.json"))

  override suspend fun getHottestPosts(page: Int): List<LobstersPost> {
    return delegate.returningResponse(hottest).getHottestPosts(page)
  }

  override suspend fun getNewestPosts(page: Int): List<LobstersPost> {
    TODO("Not yet implemented")
  }

  override suspend fun getPostDetails(postId: String): LobstersPostDetails {
    return delegate.returningResponse(postDetails).getPostDetails(postId)
  }
}
