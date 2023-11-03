/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.injection

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.optional.ForScope
import dagger.Module
import dagger.Provides
import dev.msfjarvis.claw.android.shiori.ShioriSettings

@Module
@ContributesTo(ApplicationScope::class)
object ShioriModule {
  @Provides
  fun provideShioriSettings(
    sharedPreferences: EncryptedSharedPreferences,
  ): ShioriSettings {
    return ShioriSettings(sharedPreferences)
  }

  @Provides
  fun provideSharedPreferences(
    @ForScope(ApplicationScope::class) context: Context,
  ): EncryptedSharedPreferences {
    val masterKeyAlias =
      MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
    return EncryptedSharedPreferences.create(
      context,
      "shiori_settings",
      masterKeyAlias,
      EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
      EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    ) as EncryptedSharedPreferences
  }
}
