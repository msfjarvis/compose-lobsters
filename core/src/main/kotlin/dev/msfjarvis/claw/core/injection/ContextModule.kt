/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.injection

import android.app.Application
import android.content.Context
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import dev.msfjarvis.claw.injection.scopes.AppScope

@Module
@ContributesTo(AppScope::class)
interface ContextModule {
  @get:Binds val Application.bindContext: Context
}
