/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.glance

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.net.toUri
import dev.msfjarvis.claw.android.BuildConfig
import dev.msfjarvis.claw.android.MainActivity
import io.sentry.Breadcrumb
import io.sentry.Sentry
import io.sentry.SentryLevel

/**
 * Trampoline activity for handling widget clicks with explicit components.
 *
 * This activity is required to fix COMPOSE-LOBSTERS-9X/9P/8M. Glance's LazyColumn requires all
 * actionStartActivity intents to have an explicit component set. For external URLs, we route
 * through this activity which then launches the appropriate intent (either internal deep link or
 * external browser).
 *
 * @see:
 * https://developer.android.com/reference/kotlin/androidx/glance/appwidget/action/package-summary#actionStartActivity(android.content.Intent,androidx.glance.action.ActionParameters)
 */
class WidgetClickActivity : Activity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val url = intent.getStringExtra(EXTRA_URL)
    val shortId = intent.getStringExtra(EXTRA_SHORT_ID)
    val action = intent.action

    when {
      // Handle comments deep link
      action == ACTION_OPEN_COMMENTS && shortId != null -> {
        Sentry.addBreadcrumb(
          Breadcrumb().apply {
            category = "widget_click_debug"
            message = "Comment deeplink"
            level = SentryLevel.INFO
          }
        )
        val commentsIntent =
          Intent(Intent.ACTION_VIEW, "${BuildConfig.DEEPLINK_SCHEME}://comments/$shortId".toUri())
            .apply {
              addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
              setClass(this@WidgetClickActivity, MainActivity::class.java)
            }
        startActivity(commentsIntent)
      }
      // Handle external URL
      url != null && (url.startsWith("http://") || url.startsWith("https://")) -> {
        Sentry.addBreadcrumb(
          Breadcrumb().apply {
            category = "widget_click_debug"
            message = "External URL"
            level = SentryLevel.INFO
          }
        )
        val urlIntent = Intent(Intent.ACTION_VIEW, url.toUri())
        startActivity(urlIntent)
      }
      // Handle internal/relative URL as comments link
      url != null && (url.startsWith('/') || url.isEmpty()) -> {
        Sentry.addBreadcrumb(
          Breadcrumb().apply {
            category = "widget_click_debug"
            message = "Relative URL, parsed as a comment link"
            level = SentryLevel.INFO
          }
        )
        // For internal URLs without shortId, extract it from the URL or use comments action
        val commentsIntent =
          Intent(Intent.ACTION_VIEW, "${BuildConfig.DEEPLINK_SCHEME}://comments/$shortId".toUri())
            .apply {
              addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
              setClass(this@WidgetClickActivity, MainActivity::class.java)
            }
        startActivity(commentsIntent)
      }
    }

    finish()
  }

  companion object {
    const val ACTION_OPEN_COMMENTS = "dev.msfjarvis.claw.OPEN_COMMENTS"
    const val EXTRA_URL = "dev.msfjarvis.claw.EXTRA_URL"
    const val EXTRA_SHORT_ID = "dev.msfjarvis.claw.EXTRA_SHORT_ID"
  }
}
