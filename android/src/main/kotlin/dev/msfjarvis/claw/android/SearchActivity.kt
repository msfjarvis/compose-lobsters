/*
 * Copyright © 2021-2023 Harsh Shandilya.
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
import androidx.compose.ui.platform.LocalUriHandler
import com.deliveryhero.whetstone.Whetstone
import com.deliveryhero.whetstone.activity.ContributesActivityInjector
import dev.msfjarvis.claw.android.ui.screens.SearchScreen
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import dev.msfjarvis.claw.common.comments.HTMLConverter
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import javax.inject.Inject

@ContributesActivityInjector
class SearchActivity : ComponentActivity() {

  @Inject lateinit var urlLauncher: UrlLauncher
  @Inject lateinit var htmlConverter: HTMLConverter
  @Inject lateinit var viewModel: ClawViewModel
  private var webUri: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Whetstone.inject(this)
    enableEdgeToEdge(
      statusBarStyle =
        SystemBarStyle.light(
          Color.TRANSPARENT,
          Color.TRANSPARENT,
        ),
      navigationBarStyle =
        SystemBarStyle.light(
          Color.TRANSPARENT,
          Color.TRANSPARENT,
        ),
    )
    setContent {
      LobstersTheme(
        dynamicColor = true,
        providedValues = arrayOf(LocalUriHandler provides urlLauncher),
      ) {
        SearchScreen(urlLauncher, htmlConverter, { webUri = it })
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
