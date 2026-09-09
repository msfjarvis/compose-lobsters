/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.reminders

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import dev.msfjarvis.claw.android.BuildConfig
import dev.msfjarvis.claw.android.R
import dev.msfjarvis.claw.database.local.DailySavedPostNotification
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

interface DailySavedPostReminderNotifier {
  fun isAvailable(): Boolean

  fun notify(localDate: String, selection: DailySavedPostNotification): NotificationDeliveryResult
}

enum class NotificationDeliveryResult {
  Delivered,
  Unavailable,
  Retry,
}

@Inject
@ContributesBinding(AppScope::class, binding = binding<DailySavedPostReminderNotifier>())
class AndroidDailySavedPostReminderNotifier(private val context: Context) :
  DailySavedPostReminderNotifier {

  override fun isAvailable(): Boolean {
    if (
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
          PackageManager.PERMISSION_GRANTED
    ) {
      return false
    }
    val notificationManager = NotificationManagerCompat.from(context)
    if (!notificationManager.areNotificationsEnabled()) {
      return false
    }
    createChannel()
    return getNotificationManager().getNotificationChannel(CHANNEL_ID)?.importance !=
      NotificationManager.IMPORTANCE_NONE
  }

  override fun notify(
    localDate: String,
    selection: DailySavedPostNotification,
  ): NotificationDeliveryResult {
    if (!isAvailable()) return Unavailable

    return try {
      NotificationManagerCompat.from(context)
        .notify(
          notificationTag(localDate),
          NOTIFICATION_ID,
          NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_comment)
            .setContentTitle(selection.title)
            .setContentText(context.getString(R.string.daily_saved_post_notification_text))
            .setContentIntent(createContentIntent(localDate, selection.url))
            .addAction(
              NotificationCompat.Action(
                R.drawable.ic_comment,
                context.getString(R.string.daily_saved_post_discussion_action),
                createDiscussionIntent(localDate, selection.shortId),
              )
            )
            .setAutoCancel(true)
            .build(),
        )
      NotificationDeliveryResult.Delivered
    } catch (_: SecurityException) {
      NotificationDeliveryResult.Unavailable
    } catch (_: RuntimeException) {
      NotificationDeliveryResult.Retry
    }
  }

  private fun createChannel() {
    getNotificationManager()
      .createNotificationChannel(
        NotificationChannel(
          CHANNEL_ID,
          context.getString(R.string.daily_saved_post_notification_channel_name),
          NotificationManager.IMPORTANCE_DEFAULT,
        )
      )
  }

  private fun createContentIntent(localDate: String, url: String): PendingIntent {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri()).addCategory(Intent.CATEGORY_BROWSABLE)
    return PendingIntent.getActivity(
      context,
      localDate.hashCode(),
      intent,
      PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )
  }

  private fun createDiscussionIntent(localDate: String, shortId: String): PendingIntent {
    val intent =
      Intent(Intent.ACTION_VIEW, "${BuildConfig.DEEPLINK_SCHEME}://comments/$shortId".toUri())
    return PendingIntent.getActivity(
      context,
      "$localDate:$shortId".hashCode(),
      intent,
      PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )
  }

  private fun getNotificationManager(): NotificationManager {
    return requireNotNull(context.getSystemService<NotificationManager>()) {
      "Failed to get NotificationManager"
    }
  }

  private companion object {
    const val CHANNEL_ID = "daily_saved_post_reminder"
    const val NOTIFICATION_ID = 1

    fun notificationTag(localDate: String) = "dailySavedPost:$localDate"
  }
}
