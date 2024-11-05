/*
 * Copyright © 2022-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.injection

import android.app.Application
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.crossfade
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesMultibinding
import dev.msfjarvis.claw.core.injection.AppPlugin
import javax.inject.Inject

@ContributesMultibinding(ApplicationScope::class)
class CoilAppPlugin @Inject constructor() : AppPlugin {
  override fun apply(application: Application) {
    SingletonImageLoader.setSafe {
      ImageLoader.Builder(application)
        .memoryCache {
          MemoryCache.Builder().maxSizePercent(application, MEMORY_CACHE_RATIO).build()
        }
        .diskCache {
          DiskCache.Builder()
            .directory(application.cacheDir.resolve("image_cache"))
            .maxSizeBytes(DISK_CACHE_MAX_SIZE)
            .build()
        }
        // Show a short crossfade when loading images asynchronously.
        .crossfade(true)
        .build()
    }
  }

  private companion object {
    private const val MEMORY_CACHE_RATIO = 0.25
    private const val DISK_CACHE_MAX_SIZE = 25L * 1024 * 1024 // 25 MB
  }
}
