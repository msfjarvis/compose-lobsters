/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.database

import com.google.common.truth.Truth.assertThat
import dev.msfjarvis.claw.database.local.SavedPost
import java.io.File
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import org.junit.jupiter.api.Test

@OptIn(ExperimentalSerializationApi::class)
class SavedPostSerializerTest {
  private val json = Json {
    ignoreUnknownKeys = true
    namingStrategy = JsonNamingStrategy.SnakeCase
  }
  private val text = getJson()

  @Test
  fun serialize() {
    val encoded = json.encodeToString(SavedPostSerializer(), SAVED_POST)
    assertThat(encoded).isNotEmpty()
    assertThat(encoded).isEqualTo(text)
  }

  @Test
  fun deserialize() {
    val decoded = json.decodeFromString(SavedPostSerializer(), text)
    assertThat(decoded).isEqualTo(SAVED_POST)
  }

  private fun getJson(): String {
    // Load the JSON response
    val uri = javaClass.classLoader!!.getResource("saved_post.json")
    val file = File(uri.path)
    return String(file.readBytes())
  }

  private companion object {
    private val SAVED_POST =
      SavedPost(
        title = "Fun Format Friday: You now have a super computer. What next?",
        shortId = "nbigsf",
        url = "",
        createdAt = "2023-05-04T23:43:50.000-05:00",
        commentCount = 13,
        commentsUrl = "https://lobste.rs/s/nbigsf/fun_format_friday_you_now_have_super",
        submitterName = "LenFalken",
        submitterAvatarUrl = "/avatars/LenFalken-100.png",
        tags = listOf("ask", "programming"),
        description =
          "<p>You suddenly have in your possession a super computer. What comes next? What projects are suddenly possible for you? What performance tests can you now explore?</p>\n",
      )
  }
}
