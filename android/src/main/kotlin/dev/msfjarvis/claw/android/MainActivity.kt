/*
 * Copyright © 2021-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.deliveryhero.whetstone.activity.ContributesActivityInjector
import dev.msfjarvis.claw.android.ui.screens.LobstersPostsScreen

@ContributesActivityInjector
class MainActivity : BaseActivity() {

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Composable
  override fun Content() {
    val windowSizeClass = calculateWindowSizeClass(this)
    LobstersPostsScreen(
      urlLauncher = urlLauncher,
      htmlConverter = htmlConverter,
      windowSizeClass = windowSizeClass,
      setWebUri = { url -> webUri = url },
    )
  }

  override fun preLaunch() {
    super.preLaunch()
    installSplashScreen()
  }

  companion object {
    const val NAVIGATION_KEY = "postId"
  }
}
