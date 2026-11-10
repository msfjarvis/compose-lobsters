/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.injection

import dev.msfjarvis.claw.core.network.SearchChallengeTelemetry as SearchChallengeTelemetryPort
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import io.sentry.Sentry
import io.sentry.SentryAttribute
import io.sentry.SentryAttributes
import io.sentry.metrics.SentryMetricsParameters

@BindingContainer
@ContributesTo(AppScope::class)
object SearchChallengeTelemetry {
  @Provides
  fun telemetry(): SearchChallengeTelemetryPort =
    SearchChallengeTelemetryPort { challenged, authenticated ->
      val attributes =
        SentryAttributes.of(
          SentryAttribute.stringAttribute("outcome", if (challenged) "challenge" else "other"),
          SentryAttribute.stringAttribute(
            "session",
            if (authenticated) "authenticated" else "anonymous",
          ),
        )
      Sentry.metrics()
        .count("search_html_response", 1.0, null, SentryMetricsParameters.create(attributes))
    }
}
