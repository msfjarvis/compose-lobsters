/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.injection

import android.app.Application
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.core.injection.AppPlugin
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import io.sentry.SentryOptions
import io.sentry.TypeCheckHint.OKHTTP_REQUEST
import io.sentry.android.core.SentryAndroid
import okhttp3.Request

@ContributesIntoSet(AppScope::class)
@Inject
class SentryConfigurationPlugin : AppPlugin {
  override fun apply(application: Application) {
    SentryAndroid.init(application) { options ->
      options.beforeSend =
        SentryOptions.BeforeSendCallback { event, hint ->
          val request = hint.getAs(OKHTTP_REQUEST, Request::class.java)

          // Drop all OkHttp errors that are not about Lobsters specifically.
          if (request != null && !LobstersApi.BASE_URL.contains(request.url.host)) {
            null
          } else {
            event
          }
        }
    }
  }
}
