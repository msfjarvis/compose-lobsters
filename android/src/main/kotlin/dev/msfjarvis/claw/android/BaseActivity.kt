/*
 * Copyright © 2021-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android

import android.app.assist.AssistContent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.paging.compose.LazyPagingItems
import com.deliveryhero.whetstone.Whetstone
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import dev.msfjarvis.claw.common.comments.HTMLConverter
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import dev.msfjarvis.claw.database.local.PostComments
import dev.msfjarvis.claw.model.Comment
import dev.msfjarvis.claw.model.UIPost
import javax.inject.Inject

/** A base class that encapsulates all activities used by Claw. */
@Stable
abstract class BaseActivity : ComponentActivity() {

  @Inject lateinit var urlLauncher: UrlLauncher
  @Inject lateinit var htmlConverter: HTMLConverter
  @Inject lateinit var viewModel: ClawViewModel

//  @Inject lateinit var lazyPagingItems: LazyPagingItems<UIPost>
//  @Inject lateinit var postActions: PostActions

//  @Inject lateinit var getSeenComments: (String) -> PostComments?
//  @Inject lateinit var markSeenComments: (String, List<Comment>) -> Unit
//  @Inject lateinit var openUserProfile: (String) -> Unit
//  var getSeenComments: String? = null
//  var markSeenComments: String?, List<Comment>? = null
//  var openUserProfile: String? = null

  var webUri: String? = null

  /** Entrypoint to show a [Composable] as this [ComponentActivity]'s view. */
  @Composable abstract fun Content()

  /** Overrideable method to call stuff in [onCreate]. */
  open fun preLaunch() {}

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    preLaunch()
    Whetstone.inject(this)
    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
      navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
    )
    setContent {
      LobstersTheme(
        dynamicColor = true,
        providedValues = arrayOf(LocalUriHandler provides urlLauncher),
      ) {
        Content()
      }
    }
  }

  override fun onProvideAssistContent(outContent: AssistContent?) {
    super.onProvideAssistContent(outContent)
    if (outContent != null) {
      if (webUri != null) {
        outContent.webUri = Uri.parse(webUri)
      } else {
        outContent.webUri = null
      }
    }
  }
}
