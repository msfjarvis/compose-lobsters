/*
 * Copyright © 2021 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.injection

import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import dev.msfjarvis.claw.android.ui.util.HTMLConverterImpl
import dev.msfjarvis.claw.common.comments.HTMLConverter

@Module
@ContributesTo(ApplicationScope::class)
interface HTMLConverterModule {
  @Binds fun HTMLConverterImpl.bind(): HTMLConverter
}
