/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android

import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.deliveryhero.whetstone.activity.ContributesActivityInjector
import dev.msfjarvis.claw.android.ui.screens.Nav3Screen

@ContributesActivityInjector
class MainActivity : BaseActivity() {

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Composable
  override fun Content() {
    val windowSizeClass = calculateWindowSizeClass(this)
    Nav3Screen(
      urlLauncher = urlLauncher,
      windowSizeClass = windowSizeClass,
      setWebUri = { url -> webUri = url },
    )
  }

  override fun preLaunch() {
    super.preLaunch()
    enableEdgeToEdge()
    installSplashScreen()
  }

  companion object {
    const val NAVIGATION_KEY = "postId"
  }
}
