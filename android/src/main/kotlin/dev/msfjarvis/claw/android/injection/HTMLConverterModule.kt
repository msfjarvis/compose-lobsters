/*
 * Copyright © 2021 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.injection

import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dev.msfjarvis.claw.common.comments.HTMLConverter
import io.github.furstenheim.CopyDown

@Module
@ContributesTo(ApplicationScope::class)
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
