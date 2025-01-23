/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.injection

import android.app.Application

/** Basic plugin-based architecture for doing things at [Application] initialization. */
interface AppPlugin {
  fun apply(application: Application)
}
