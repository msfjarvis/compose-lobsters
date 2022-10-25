/*
 * Copyright © 2021 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.injection

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.msfjarvis.claw.common.comments.HTMLConverter
import io.github.furstenheim.CopyDown

@Module
@InstallIn(SingletonComponent::class)
object HTMLConverterModule {

  @Provides
  fun provideHTMLConverter() =
    object : HTMLConverter {
      private val copydown = CopyDown()

      override fun convertHTMLToMarkdown(html: String): String {
        return copydown.convert(html)
      }
    }
}
