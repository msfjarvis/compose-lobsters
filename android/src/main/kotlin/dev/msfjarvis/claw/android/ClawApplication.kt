package dev.msfjarvis.claw.android

import android.app.Application
import android.os.StrictMode
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ClawApplication : Application(), Configuration.Provider {
  @Inject lateinit var workerFactory: HiltWorkerFactory

  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
      StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build())
      StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build())
    }
  }

  override fun getWorkManagerConfiguration(): Configuration {
    return Configuration.Builder()
      .setMinimumLoggingLevel(Log.DEBUG)
      .setWorkerFactory(workerFactory)
      .build()
  }
}
