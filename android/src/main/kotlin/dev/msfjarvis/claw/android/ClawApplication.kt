/*
 * Copyright © 2021-2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android

import android.app.Application
import android.os.StrictMode
import android.util.Log
import androidx.work.Configuration
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dev.msfjarvis.claw.injection.Components
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import tangle.inject.TangleGraph

class ClawApplication : Application(), Configuration.Provider, ImageLoaderFactory {

  override fun onCreate() {
    super.onCreate()
    val component = DaggerAppComponent.factory().create(this)
    Components.add(component)
    TangleGraph.add(component)
    StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build())
    StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build())
    Napier.base(DebugAntilog())
  }

  override fun getWorkManagerConfiguration(): Configuration {
    return Configuration.Builder().setMinimumLoggingLevel(Log.DEBUG).build()
  }

  override fun newImageLoader(): ImageLoader {
    return ImageLoader.Builder(this)
      .memoryCache { MemoryCache.Builder(this).maxSizePercent(MEMORY_CACHE_RATIO).build() }
      .diskCache {
        DiskCache.Builder()
          .directory(cacheDir.resolve("image_cache"))
          .maxSizeBytes(DISK_CACHE_MAX_SIZE)
          .build()
      }
      // Show a short crossfade when loading images asynchronously.
      .crossfade(true)
      .build()
  }

  private companion object {
    private const val MEMORY_CACHE_RATIO = 0.25
    private const val DISK_CACHE_MAX_SIZE = 25L * 1024 * 1024 // 25 MB
  }
}
