/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments.reply

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.performTextReplacement
import dev.msfjarvis.claw.api.AuthenticatedLobstersApi
import dev.msfjarvis.claw.api.LobstersApi
import java.lang.reflect.Proxy
import kotlinx.coroutines.Dispatchers
import org.junit.Rule
import org.junit.Test

class ReplyRestorationTest {
  @get:Rule val compose = createComposeRule()

  @Test
  fun draftRestoresIntoNewViewModel() {
    val api =
      Proxy.newProxyInstance(
        LobstersApi::class.java.classLoader,
        arrayOf(LobstersApi::class.java),
      ) { _, _, _ ->
        error("The API should not be called")
      } as LobstersApi
    fun newViewModel() = ReplyViewModel(AuthenticatedLobstersApi(api), Dispatchers.IO)
    var viewModel = newViewModel()
    val restoration = StateRestorationTester(compose)
    restoration.setContent {
      ReplyScreen(
        postId = "post",
        commentId = "comment",
        commentText = "Original comment",
        onReplySubmitted = {},
        contentPadding = PaddingValues(),
        viewModel = viewModel,
      )
    }
    compose.onNode(hasSetTextAction()).performTextReplacement("unfinished reply")
    compose.runOnIdle { viewModel = newViewModel() }
    restoration.emulateSavedInstanceStateRestore()
    compose.onNode(hasSetTextAction()).assertTextContains("unfinished reply")
  }
}
