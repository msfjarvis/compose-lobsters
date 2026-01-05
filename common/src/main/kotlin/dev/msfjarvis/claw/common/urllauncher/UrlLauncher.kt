/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.urllauncher

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.platform.UriHandler
import androidx.core.net.toUri
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class UrlLauncher(private val context: Context) : UriHandler {
  override fun openUri(uri: String) {
    val customTabsIntent =
      CustomTabsIntent.Builder()
        .setShareState(CustomTabsIntent.SHARE_STATE_ON)
        .setShowTitle(true)
        .setColorScheme(CustomTabsIntent.COLOR_SCHEME_SYSTEM)
        .build()
    // Workaround for starting activity from non-activity context, this should be reverted once
    // we've extended Metro to have an Activity scoped context.
    val intent = Intent(customTabsIntent.intent)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.setData(uri.toUri())
    try {
      context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
      val error = "Failed to open URL: $uri"
      Log.d("UrlLauncher", error, e)
      Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }
  }
}
