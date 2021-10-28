package dev.msfjarvis.claw.common.urllauncher

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.platform.UriHandler

class UrlLauncher(private val context: Context) : UriHandler {
  override fun openUri(uri: String) {
    val customTabsIntent =
      CustomTabsIntent.Builder()
        .setShareState(CustomTabsIntent.SHARE_STATE_ON)
        .setShowTitle(true)
        .setColorScheme(CustomTabsIntent.COLOR_SCHEME_DARK)
        .build()
    customTabsIntent.launchUrl(context, Uri.parse(uri))
  }
}
