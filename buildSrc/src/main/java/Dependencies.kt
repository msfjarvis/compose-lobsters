/*
 * Copyright © 2014-2020 The Android Password Store Authors. All Rights Reserved.
 * SPDX-License-Identifier: GPL-3.0-only
 */

private const val DAGGER_HILT_VERSION = "2.33-beta"

object Plugins {
  const val android = "com.android.tools.build:gradle:7.0.0-alpha11"
  const val lintModel = "com.android.tools.lint:lint-model:30.0.0-alpha11"
  const val hilt = "com.google.dagger:hilt-android-gradle-plugin:${DAGGER_HILT_VERSION}"
  const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.31"
  const val jsemver = "com.github.zafarkhaja:java-semver:0.9.0"
  const val shot = "com.karumi:shot:5.10.3"
  const val sqldelight = "com.squareup.sqldelight:gradle-plugin:1.4.4"
}

object Dependencies {
  const val COMPOSE_VERSION = "1.0.0-beta02"

  object Kotlin {

    object Coroutines {

      private const val version = "1.4.3"
      const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
      const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
      const val jvmCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$version"
    }
  }

  object AndroidX {

    const val appCompat = "androidx.appcompat:appcompat:1.3.0-beta01"
    const val browser = "androidx.browser:browser:1.3.0"
    const val coreLibraryDesugaring = "com.android.tools:desugar_jdk_libs:1.0.10"
    const val datastore = "androidx.datastore:datastore-preferences:1.0.0-alpha08"

    object Compose {

      const val activity = "androidx.activity:activity-compose:1.3.0-alpha04"
      const val compiler = "androidx.compose.compiler:compiler:$COMPOSE_VERSION"
      const val foundation = "androidx.compose.foundation:foundation:$COMPOSE_VERSION"
      const val foundationLayout = "androidx.compose.foundation:foundation-layout:$COMPOSE_VERSION"
      const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha03"
      const val material = "androidx.compose.material:material:$COMPOSE_VERSION"
      const val navigation = "androidx.navigation:navigation-compose:1.0.0-alpha09"
      const val paging = "androidx.paging:paging-compose:1.0.0-alpha08"
      const val runtime = "androidx.compose.runtime:runtime:$COMPOSE_VERSION"
      const val ui = "androidx.compose.ui:ui:$COMPOSE_VERSION"
      const val uiUnit = "androidx.compose.ui:ui-unit:$COMPOSE_VERSION"
      const val uiTooling = "androidx.compose.ui:ui-tooling:$COMPOSE_VERSION"
    }

    object Hilt {
      const val dagger = "com.google.dagger:hilt-android:$DAGGER_HILT_VERSION"
      const val daggerCompiler = "com.google.dagger:hilt-compiler:$DAGGER_HILT_VERSION"
    }

    object Lifecycle {

      private const val version = "2.3.0"
      const val runtimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
      const val viewmodelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
    }
  }

  object ThirdParty {

    const val accompanist = "dev.chrisbanes.accompanist:accompanist-coil:0.6.2"
    const val composeFlowLayout = "com.star-zero:compose-flowlayout:0.0.1"
    const val kamel = "com.alialbaali.kamel:kamel-image:0.2.0"
    const val pullToRefresh = "com.puculek.pulltorefresh:pull-to-refresh-compose:1.0.1"

    object Moshi {

      const val lib = "com.squareup.moshi:moshi:1.11.0"
      const val ksp = "dev.zacsweers.moshix:moshi-ksp:0.9.2"
    }

    object Retrofit {

      private const val version = "2.9.0"
      const val lib = "com.squareup.retrofit2:retrofit:$version"
      const val moshi = "com.squareup.retrofit2:converter-moshi:$version"
    }

    object SQLDelight {

      private const val version = "1.4.4"
      const val jvmDriver = "com.squareup.sqldelight:sqlite-driver:$version"
      const val androidDriver = "com.squareup.sqldelight:android-driver:$version"
    }
  }

  object Testing {

    const val mockWebServer = "com.squareup.okhttp3:mockwebserver3-junit4:5.0.0-alpha.2"
  }
}
