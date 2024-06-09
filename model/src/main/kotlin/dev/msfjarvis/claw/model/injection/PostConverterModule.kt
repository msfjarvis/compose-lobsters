/*
 * Copyright © 2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.model.injection

import dagger.Binds
import dagger.Module
import dev.msfjarvis.claw.model.PostConverter
import dev.msfjarvis.claw.model.PostConverterImpl

@Module
interface PostConverterModule {
  @Binds fun bindPostsConverter(impl: PostConverterImpl): PostConverter
}
