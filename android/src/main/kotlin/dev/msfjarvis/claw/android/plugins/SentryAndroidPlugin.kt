/*
 * Copyright © 2022-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.plugins

import android.app.Application
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesMultibinding
import dev.msfjarvis.claw.core.injection.AppPlugin
import io.sentry.android.core.SentryAndroid
import javax.inject.Inject

@ContributesMultibinding(ApplicationScope::class)
class SentryAndroidPlugin @Inject constructor() : AppPlugin {
  override fun apply(application: Application) {
    SentryAndroid.init(application) { options ->
      options.experimental.sessionReplay.onErrorSampleRate = 1.0
      options.experimental.sessionReplay.sessionSampleRate = 0.1
    }
  }
}
