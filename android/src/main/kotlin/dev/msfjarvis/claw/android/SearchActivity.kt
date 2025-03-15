/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android

import androidx.compose.runtime.Composable
import com.deliveryhero.whetstone.activity.ContributesActivityInjector
import dev.msfjarvis.claw.android.ui.screens.SearchScreen

@ContributesActivityInjector
class SearchActivity : BaseActivity() {
  @Composable
  override fun Content() {
    SearchScreen(urlLauncher = urlLauncher, setWebUri = { webUri = it }, viewModel = viewModel)
  }
}
